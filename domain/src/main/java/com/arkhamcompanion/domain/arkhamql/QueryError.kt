package com.arkhamcompanion.domain.arkhamql

import com.arkhamcompanion.domain.arkhamql.ast.QueryFieldType
import com.arkhamcompanion.domain.arkhamql.lexer.QueryToken

sealed interface QueryError {

    //Lexer errors
    data class UnterminatedStringLiteral(
        val position: Int,
    ) : QueryError

    data class UnterminatedRegexLiteral(
        val position: Int,
    ) : QueryError

    data class UnexpectedCharacter(
        val value: Char,
        val position: Int,
    ) : QueryError

    //Parser errors
    data object ComparisonOperatorsCannotBeChained : QueryError

    data class ExpectedValueOrField(
        val token: QueryToken,
        val position: Int,
    ) : QueryError

    data class EmptyFieldReference(
        val position: Int,
    ) : QueryError

    data class DuplicateQualifier(
        val qualifier: String,
    ) : QueryError

    data class FieldNameIsMissing(
        val position: Int,
    ) : QueryError

    data class UnknownField(
        val fieldName: String,
    ) : QueryError

    data class ExpectedType(
        val value: String,
        val token: QueryToken,
        val position: Int,
    ) : QueryError

    data class UnexpectedToken(
        val token: QueryToken,
        val position: Int,
    ) : QueryError

    //Evaluation errors
    data object ExpressionMustContainAnOperator : QueryError

    data object ArithmeticExpressionCannotBeEvaluatedAsBoolean : QueryError

    data class ExpressionDoesNotProduceValue(
        val operator: String,
    ) : QueryError

    data object CannotEvaluateListAsSingleValue : QueryError

    data object ExpectedListExpression : QueryError

    data class ExpectedNumberOnLeftSide(
        val operator: String,
    ) : QueryError

    data class ExpectedNumberOnRightSide(
        val operator: String,
    ) : QueryError

    data object DivisionByZero : QueryError

    data object ModuloByZero : QueryError

    data class NotArithmeticOperator(
        val operator: String,
    ) : QueryError

    data class InvalidRegex(
        val pattern: String,
    ) : QueryError

    data class CannotConvertValueToNumber(
        val value: String,
    ) : QueryError

    data object CannotConvertBooleanToNumber : QueryError

    data object CannotConvertRegexToNumber : QueryError

    data class TypeMismatch(
        val leftType: QueryFieldType,
        val rightType: QueryFieldType,
    ) : QueryError

    data object ExpectedSingleValueGotMultipleValues : QueryError

    data class UnknownError(
        val message: String,
    ) : QueryError
}