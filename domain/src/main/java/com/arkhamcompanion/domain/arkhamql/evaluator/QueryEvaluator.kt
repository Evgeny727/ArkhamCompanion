package com.arkhamcompanion.domain.arkhamql.evaluator

import com.arkhamcompanion.domain.arkhamql.ast.QueryBinaryOperator
import com.arkhamcompanion.domain.arkhamql.ast.QueryExpression
import com.arkhamcompanion.domain.arkhamql.ast.QueryFieldType
import com.arkhamcompanion.domain.arkhamql.ast.QueryValue
import com.arkhamcompanion.domain.objects.FuzzyMatcher.matchesFuzzy
import com.arkhamcompanion.domain.objects.splitQueryToWords

class QueryEvaluator<T>(
    private val fieldResolver: QueryFieldResolver<T>,
    private val matchBacks: Boolean = false,
    private val matchReal: Boolean = false,
) {

    fun evaluate(
        expression: QueryExpression,
        card: T,
    ): Boolean {
        return evaluateBoolean(expression, card)
    }

    private fun evaluateBoolean(
        expression: QueryExpression,
        card: T,
    ): Boolean {
        return when (expression) {
            is QueryExpression.Binary -> evaluateBinary(
                expression = expression,
                card = card,
            )

            is QueryExpression.Field,
            is QueryExpression.Literal,
            is QueryExpression.List -> {
                throw QueryEvaluationException(
                    "Expression must contain an operator",
                )
            }
        }
    }

    private fun evaluateBinary(
        expression: QueryExpression.Binary,
        card: T,
    ): Boolean {
        val left = expression.left
        val right = expression.right
        val operator = expression.operator

        validateTypes(left, right)

        return when (operator) {
            QueryBinaryOperator.AND -> {
                evaluateBoolean(left, card) &&
                        evaluateBoolean(right, card)
            }

            QueryBinaryOperator.OR -> {
                evaluateBoolean(left, card) ||
                        evaluateBoolean(right, card)
            }

            QueryBinaryOperator.EQUALS -> {
                evaluateEquals(
                    left = left,
                    right = right,
                    card = card,
                    strict = false,
                )
            }

            QueryBinaryOperator.NOT_EQUALS -> {
                !evaluateEquals(
                    left = left,
                    right = right,
                    card = card,
                    strict = false,
                )
            }

            QueryBinaryOperator.EXACT_EQUALS -> {
                evaluateEquals(
                    left = left,
                    right = right,
                    card = card,
                    strict = true,
                )
            }

            QueryBinaryOperator.EXACT_NOT_EQUALS -> {
                !evaluateEquals(
                    left = left,
                    right = right,
                    card = card,
                    strict = true,
                )
            }

            QueryBinaryOperator.CONTAINS -> {
                evaluateContains(
                    left = left,
                    right = right,
                    card = card,
                    strict = false,
                )
            }

            QueryBinaryOperator.NOT_CONTAINS -> {
                !evaluateContains(
                    left = left,
                    right = right,
                    card = card,
                    strict = false,
                )
            }

            QueryBinaryOperator.EXACT_CONTAINS -> {
                evaluateContains(
                    left = left,
                    right = right,
                    card = card,
                    strict = true,
                )
            }

            QueryBinaryOperator.EXACT_NOT_CONTAINS -> {
                !evaluateContains(
                    left = left,
                    right = right,
                    card = card,
                    strict = true,
                )
            }

            QueryBinaryOperator.GREATER_THAN -> {
                evaluateNumericComparison(
                    left = left,
                    right = right,
                    card = card,
                ) { a, b ->
                    a > b
                }
            }

            QueryBinaryOperator.LESS_THAN -> {
                evaluateNumericComparison(
                    left = left,
                    right = right,
                    card = card,
                ) { a, b ->
                    a < b
                }
            }

            QueryBinaryOperator.GREATER_OR_EQUAL -> {
                evaluateNumericComparison(
                    left = left,
                    right = right,
                    card = card,
                ) { a, b ->
                    a >= b
                }
            }

            QueryBinaryOperator.LESS_OR_EQUAL -> {
                evaluateNumericComparison(
                    left = left,
                    right = right,
                    card = card,
                ) { a, b ->
                    a <= b
                }
            }

            QueryBinaryOperator.ADD,
            QueryBinaryOperator.SUBTRACT,
            QueryBinaryOperator.MULTIPLY,
            QueryBinaryOperator.DIVIDE,
            QueryBinaryOperator.MODULO -> {
                throw QueryEvaluationException(
                    "Arithmetic expression cannot be evaluated as boolean",
                )
            }
        }
    }

    private fun evaluateEquals(
        left: QueryExpression,
        right: QueryExpression,
        card: T,
        strict: Boolean,
    ): Boolean {
        val leftValues = evaluateValues(
            expression = left,
            card = card,
        )

        val rightValues = evaluateValues(
            expression = right,
            card = card,
        )

        return compareValues(
            left = leftValues,
            right = rightValues,
            strict = strict,
            fieldType = fieldType(left),
        )
    }

    private fun evaluateContains(
        left: QueryExpression,
        right: QueryExpression,
        card: T,
        strict: Boolean,
    ): Boolean {
        val rightValues = evaluateList(
            expression = right,
            card = card,
        )

        val leftValues = evaluateValues(
            expression = left,
            card = card,
        )

        return rightValues.any { rightValue ->
            compareValues(
                left = leftValues,
                right = EvaluatedValues.Single(rightValue),
                strict = strict,
                fieldType = fieldType(left),
            )
        }
    }

    private fun evaluateNumericComparison(
        left: QueryExpression,
        right: QueryExpression,
        card: T,
        compare: (Int, Int) -> Boolean,
    ): Boolean {
        val leftValues = evaluateValues(
            expression = left,
            card = card,
        )

        val rightValues = evaluateValues(
            expression = right,
            card = card,
        )

        /*
         * A multi-value numeric field follows the same rule as other
         * multi-value fields: if any individual value satisfies the
         * comparison, the field matches.
         */
        return anyPair(
            left = leftValues,
            right = rightValues,
        ) { leftValue, rightValue ->
            val leftNumber = toNumber(leftValue)
            val rightNumber = toNumber(rightValue)

            if (leftNumber == null || rightNumber == null) {
                false
            } else {
                compare(leftNumber, rightNumber)
            }
        }
    }

    private fun evaluateValues(
        expression: QueryExpression,
        card: T,
    ): EvaluatedValues {
        return when (expression) {
            is QueryExpression.Literal -> {
                EvaluatedValues.Single(
                    expression.value,
                )
            }

            is QueryExpression.Field -> {
                evaluateField(
                    expression = expression,
                    card = card,
                )
            }

            is QueryExpression.Binary -> {
                if (!isArithmetic(expression.operator)) {
                    throw QueryEvaluationException(
                        "Expression does not produce a value: " +
                                expression.operator,
                    )
                }

                EvaluatedValues.Single(
                    evaluateArithmeticValue(
                        expression = expression,
                        card = card,
                    ),
                )
            }

            is QueryExpression.List -> {
                throw QueryEvaluationException(
                    "Cannot evaluate a list as a single value",
                )
            }
        }
    }

    private fun evaluateField(
        expression: QueryExpression.Field,
        card: T,
    ): EvaluatedValues {
        return fieldResolver.resolve(
            reference = expression.reference,
            card = card,
            matchBacks = matchBacks,
            matchReal = matchReal,
        )
    }

    private fun evaluateList(
        expression: QueryExpression,
        card: T,
    ): List<QueryValue> {
        if (expression !is QueryExpression.List) {
            throw QueryEvaluationException(
                "Expected list expression",
            )
        }

        return expression.elements.map { element ->
            evaluateValues(
                expression = element,
                card = card,
            ).singleValue()
        }
    }

    private fun evaluateArithmeticValue(
        expression: QueryExpression.Binary,
        card: T,
    ): QueryValue.Number {
        val left = evaluateValues(
            expression = expression.left,
            card = card,
        ).singleValue()

        val right = evaluateValues(
            expression = expression.right,
            card = card,
        ).singleValue()

        val leftNumber = toNumber(left)
            ?: throw QueryEvaluationException(
                "Expected number on left side of ${expression.operator}",
            )

        val rightNumber = toNumber(right)
            ?: throw QueryEvaluationException(
                "Expected number on right side of ${expression.operator}",
            )

        val result = when (expression.operator) {
            QueryBinaryOperator.ADD ->
                leftNumber + rightNumber

            QueryBinaryOperator.SUBTRACT ->
                leftNumber - rightNumber

            QueryBinaryOperator.MULTIPLY ->
                leftNumber * rightNumber

            QueryBinaryOperator.DIVIDE -> {
                if (rightNumber == 0) {
                    throw QueryEvaluationException(
                        "Division by zero",
                    )
                }

                leftNumber / rightNumber
            }

            QueryBinaryOperator.MODULO -> {
                if (rightNumber == 0) {
                    throw QueryEvaluationException(
                        "Modulo by zero",
                    )
                }

                leftNumber % rightNumber
            }

            else -> {
                throw QueryEvaluationException(
                    "Not an arithmetic operator: ${expression.operator}",
                )
            }
        }

        return QueryValue.Number(result)
    }

    private fun compareValues(
        left: EvaluatedValues,
        right: EvaluatedValues,
        strict: Boolean,
        fieldType: QueryFieldType?,
    ): Boolean {
        return anyPair(
            left = left,
            right = right,
        ) { leftValue, rightValue ->
            compareSingleValues(
                left = leftValue,
                right = rightValue,
                strict = strict,
                fieldType = fieldType,
            )
        }
    }

    private fun compareSingleValues(
        left: QueryValue,
        right: QueryValue,
        strict: Boolean,
        fieldType: QueryFieldType?,
    ): Boolean {
        /*
         * Regex can only meaningfully match strings.
         */
        if (right is QueryValue.Regex) {
            return left is QueryValue.String &&
                regexMatches(
                    pattern = right.pattern,
                    text = left.value,
                )
        }

        if (left is QueryValue.Regex) {
            return right is QueryValue.String &&
                    regexMatches(
                        pattern = left.pattern,
                        text = right.value,
                    )
        }

        /*
         * Number fields can contain the special string values:
         *
         * "-" -> null
         * "x" -> -2
         * "*" -> -3
         * "?" -> -4
         */
        if (
            fieldType == QueryFieldType.NUMBER &&
            isNumberLike(left) &&
            isNumberLike(right)
        ) {
            return toNumber(left) == toNumber(right)
        }

        if (
            left is QueryValue.Boolean &&
            right is QueryValue.Boolean
        ) {
            return left.value == right.value
        }

        if (
            left is QueryValue.String &&
            right is QueryValue.String
        ) {
            return compareStrings(
                left = left.value,
                right = right.value,
                strict = strict,
            )
        }

        if (isNullish(left) || isNullish(right)) {
            return isNullish(left) && isNullish(right)
        }

        return false
    }

    private fun isNumberLike(value: QueryValue): Boolean =
        value is QueryValue.Number ||
                value is QueryValue.String ||
                value is QueryValue.Null

    private fun compareStrings(
        left: String,
        right: String,
        strict: Boolean,
    ): Boolean {
        val normalizedLeft = left.lowercase().trim()
        val normalizedRight = right.lowercase().trim()

        /*
         * For loose equality (=), use the existing fuzzy search.
         *
         * The RHS is the query and the LHS is the text being searched.
         */
        if (!strict) {
            val queryWords = normalizedRight.splitQueryToWords()

            return matchesFuzzy(
                text = normalizedLeft,
                queryWords = queryWords,
            )
        }

        return normalizedLeft.contains(normalizedRight)
    }

    private fun regexMatches(
        pattern: String,
        text: String,
    ): Boolean {
        return try {
            Regex(
                pattern = pattern,
                option = RegexOption.IGNORE_CASE,
            ).containsMatchIn(text)
        } catch (e: Exception) {
            throw QueryEvaluationException(
                "Invalid regex: /$pattern/",
            )
        }
    }

    private fun toNumber(
        value: QueryValue,
    ): Int? {
        return when (value) {
            is QueryValue.Number ->
                value.value

            is QueryValue.String -> {
                when (value.value.trim().lowercase()) {
                    "-" -> null
                    "x" -> -2
                    "*" -> -3
                    "?" -> -4

                    else -> {
                        value.value
                            .trim()
                            .toIntOrNull()
                            ?: throw QueryEvaluationException(
                                "Cannot convert \"${value.value}\" to number",
                            )
                    }
                }
            }

            QueryValue.Null ->
                null

            is QueryValue.Boolean ->
                throw QueryEvaluationException(
                    "Cannot convert boolean to number",
                )

            is QueryValue.Regex ->
                throw QueryEvaluationException(
                    "Cannot convert regex to number",
                )
        }
    }

    private fun isTruthy(
        value: QueryValue,
    ): Boolean {
        return when (value) {
            QueryValue.Null ->
                false

            is QueryValue.Boolean ->
                value.value

            is QueryValue.Number ->
                value.value != 0

            is QueryValue.String ->
                value.value.isNotEmpty()

            is QueryValue.Regex ->
                true
        }
    }

    private fun isNullish(
        value: QueryValue,
    ): Boolean {
        return value is QueryValue.Null ||
                value is QueryValue.String && value.value.isEmpty()
    }

    private fun isArithmetic(
        operator: QueryBinaryOperator,
    ): Boolean {
        return operator in setOf(
            QueryBinaryOperator.ADD,
            QueryBinaryOperator.SUBTRACT,
            QueryBinaryOperator.MULTIPLY,
            QueryBinaryOperator.DIVIDE,
            QueryBinaryOperator.MODULO,
        )
    }

    private fun fieldType(
        expression: QueryExpression,
    ): QueryFieldType? {
        return when (expression) {
            is QueryExpression.Field ->
                expression.reference.field.type

            else ->
                null
        }
    }

    private fun validateTypes(
        left: QueryExpression,
        right: QueryExpression,
    ) {
        val leftType = fieldType(left)
        val rightType = fieldType(right)

        if (
            leftType != null &&
            rightType != null &&
            leftType != rightType
        ) {
            throw QueryEvaluationException(
                "Type mismatch: cannot compare " +
                        "$leftType field with $rightType field",
            )
        }
    }

    private fun anyPair(
        left: EvaluatedValues,
        right: EvaluatedValues,
        predicate: (QueryValue, QueryValue) -> Boolean,
    ): Boolean {
        val leftValues = left.asList()
        val rightValues = right.asList()

        return leftValues.any { leftValue ->
            rightValues.any { rightValue ->
                predicate(leftValue, rightValue)
            }
        }
    }

    private fun EvaluatedValues.asList(): List<QueryValue> {
        return when (this) {
            is EvaluatedValues.Single ->
                listOf(value)

            is EvaluatedValues.Multiple ->
                values
        }
    }

    private fun EvaluatedValues.singleValue(): QueryValue {
        return when (this) {
            is EvaluatedValues.Single ->
                value

            is EvaluatedValues.Multiple -> {
                throw QueryEvaluationException(
                    "Expected a single value, got multiple values",
                )
            }
        }
    }
}