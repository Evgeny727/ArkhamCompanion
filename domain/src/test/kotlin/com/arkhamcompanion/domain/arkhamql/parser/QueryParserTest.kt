package com.arkhamcompanion.domain.arkhamql.parser

import com.arkhamcompanion.domain.arkhamql.ast.QueryBinaryOperator
import com.arkhamcompanion.domain.arkhamql.ast.QueryExpression
import com.arkhamcompanion.domain.arkhamql.ast.QueryField
import com.arkhamcompanion.domain.arkhamql.ast.QueryFieldReference
import com.arkhamcompanion.domain.arkhamql.ast.QueryValue
import com.arkhamcompanion.domain.arkhamql.fields.QueryFieldRegistry
import com.arkhamcompanion.domain.arkhamql.fields.QueryFields
import com.arkhamcompanion.domain.arkhamql.lexer.QueryLexer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class QueryParserTest {

    private val fieldRegistry = QueryFieldRegistry(QueryFields.all)

    private fun parse(query: String): QueryExpression =
        QueryParser(
            tokens = QueryLexer(query).tokenize(),
            fieldRegistry = fieldRegistry,
        ).parse()

    private fun field(
        field: QueryField,
        real: Boolean = false,
        back: Boolean = false,
    ): QueryExpression = QueryExpression.Field(
        QueryFieldReference(
            field = field,
            real = real,
            back = back,
        )
    )

    private fun string(value: String): QueryExpression =
        QueryExpression.Literal( QueryValue.String(value) )

    private fun number(value: Int): QueryExpression =
        QueryExpression.Literal( QueryValue.Number(value) )

    private fun boolean(value: Boolean): QueryExpression =
        QueryExpression.Literal( QueryValue.Boolean(value) )

    private val nullValue: QueryExpression
        get() = QueryExpression.Literal(QueryValue.Null)

    private fun list(vararg values: QueryExpression): QueryExpression =
        QueryExpression.List(values.toList())

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    @Test
    fun `parses simple field`() {
        assertEquals(
            field(QueryFields.health),
            parse("health"),
        )
    }

    @Test
    fun `parses field case insensitively`() {
        assertEquals(
            field(QueryFields.health),
            parse("HeAlTh"),
        )
    }

    @Test
    fun `parses back field`() {
        assertEquals(
            field( field = QueryFields.text, back = true),
            parse("back:text"),
        )
    }

    @Test
    fun `parses real field`() {
        assertEquals(
            field( field = QueryFields.text, real = true),
            parse("real:text"),
        )
    }

    @Test
    fun `parses real back field`() {
        assertEquals(
            field( field = QueryFields.text, real = true, back = true),
            parse("real:back:text"),
        )
    }

    @Test
    fun `parses back real field`() {
        assertEquals(
            field( field = QueryFields.text, real = true, back = true),
            parse("back:real:text"),
        )
    }

    // -------------------------------------------------------------------------
    // Literals
    // -------------------------------------------------------------------------

    @Test
    fun `parses string literal`() {
        assertEquals(
            string("fight"),
            parse("\"fight\""),
        )
    }

    @Test fun `parses number literal`() {
        assertEquals(
            number(42),
            parse("42"),
        )
    }

    @Test fun `parses negative number literal`() {
        assertEquals(
            number(-42),
            parse("-42"),
        )
    }

    @Test fun `parses boolean true`() {
        assertEquals(
            boolean(true),
            parse("true"),
        )
    }

    @Test fun `parses boolean false`() {
        assertEquals(
            boolean(false),
            parse("false"),
        )
    }

    @Test fun `parses null`() {
        assertEquals(
            nullValue,
            parse("null"),
        )
    }

    @Test fun `parses regex literal`() {
        assertEquals(
            QueryExpression.Literal( QueryValue.Regex("""\d+ damage""") ),
            parse("""/\d+ damage/"""),
        )
    }

    // -------------------------------------------------------------------------
    // Binary operators
    // -------------------------------------------------------------------------

    @Test fun `parses equals`() {
        assertBinary(
            query = """health = 5""",
            operator = QueryBinaryOperator.EQUALS,
            right = number(5),
        )
    }

    @Test fun `parses exact equals`() {
        assertBinary(
            query = """health == 5""",
            operator = QueryBinaryOperator.EXACT_EQUALS,
            right = number(5),
        )
    }

    @Test fun `parses not equals`() {
        assertBinary(
            query = """health != 5""",
            operator = QueryBinaryOperator.NOT_EQUALS,
            right = number(5),
        )
    }

    @Test fun `parses exact not equals`() {
        assertBinary(
            query = """health !== 5""",
            operator = QueryBinaryOperator.EXACT_NOT_EQUALS,
            right = number(5),
        )
    }

    @Test fun `parses contains`() {
        assertBinary(
            query = """text ? "fight"""",
            operator = QueryBinaryOperator.CONTAINS,
            right = string("fight"),
            left = field(QueryFields.text),
        )
    }

    @Test fun `parses exact contains`() {
        assertBinary(
            query = """text ?? "fight"""",
            operator = QueryBinaryOperator.EXACT_CONTAINS,
            right = string("fight"),
            left = field(QueryFields.text),
        )
    }

    @Test fun `parses not contains`() {
        assertBinary(
            query = """text !? "fight"""",
            operator = QueryBinaryOperator.NOT_CONTAINS,
            right = string("fight"),
            left = field(QueryFields.text),
        )
    }

    @Test fun `parses exact not contains`() {
        assertBinary(
            query = """text !?? "fight"""",
            operator = QueryBinaryOperator.EXACT_NOT_CONTAINS,
            right = string("fight"),
            left = field(QueryFields.text),
        )
    }

    @Test fun `parses greater than`() {
        assertBinary(
            query = "health > 5",
            operator = QueryBinaryOperator.GREATER_THAN,
            right = number(5),
        )
    }

    @Test fun `parses greater or equal`() {
        assertBinary(
            query = "health >= 5",
            operator = QueryBinaryOperator.GREATER_OR_EQUAL,
            right = number(5),
        )
    }

    @Test fun `parses less than`() {
        assertBinary(
            query = "health < 5",
            operator = QueryBinaryOperator.LESS_THAN,
            right = number(5),
        )
    }

    @Test fun `parses less or equal`() {
        assertBinary(
            query = "health <= 5",
            operator = QueryBinaryOperator.LESS_OR_EQUAL,
            right = number(5),
        )
    }

    // -------------------------------------------------------------------------
    // Binary operands
    // -------------------------------------------------------------------------

    @Test fun `parses field compared with field`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.health),
                operator = QueryBinaryOperator.GREATER_THAN,
                right = field(QueryFields.fight),
            ),
            parse("health > fight"),
        )
    }

    @Test fun `parses field compared with Binary expression`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.health),
                operator = QueryBinaryOperator.GREATER_THAN,
                right = QueryExpression.Binary(
                    left = field(QueryFields.fight),
                    operator = QueryBinaryOperator.ADD,
                    right = number(1),
                ),
            ),
            parse("health > fight + 1"),
        )
    }

    @Test fun `parses Binary expression compared with field`() {
        assertEquals(
            QueryExpression.Binary(
                left = QueryExpression.Binary(
                    left = field(QueryFields.fight),
                    operator = QueryBinaryOperator.ADD,
                    right = number(1),
                ),
                operator = QueryBinaryOperator.GREATER_THAN,
                right = field(QueryFields.health),
            ),
            parse("fight + 1 > health"),
        )
    }

    @Test fun `parses Binary with null`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.health),
                operator = QueryBinaryOperator.EQUALS,
                right = nullValue,
            ),
            parse("health = null"),
        )
    }

    @Test
    fun `parses single Binary`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.health),
                operator = QueryBinaryOperator.GREATER_THAN,
                right = number(5),
            ),
            parse("health > 5"),
        )
    }

    @Test
    fun `rejects chained Binary`() {
        assertThrows(QueryParserException::class.java) {
            parse("health > 5 > 2")
        }
    }

    @Test
    fun `rejects chained equality Binary`() {
        assertThrows(QueryParserException::class.java) {
            parse("health = 5 = 2")
        }
    }

    @Test
    fun `rejects chained contains Binary`() {
        assertThrows(QueryParserException::class.java) {
            parse("""text ? "fight" ? "damage"""")
        }
    }

    @Test
    fun `allows chained arithmetic operators`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.fight),
                operator = QueryBinaryOperator.ADD,
                right = QueryExpression.Binary(
                    left = number(1),
                    operator = QueryBinaryOperator.MULTIPLY,
                    right = number(2),
                ),
            ),
            parse("fight + 1 * 2"),
        )
    }

    @Test
    fun `parses addition`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.fight),
                operator = QueryBinaryOperator.ADD,
                right = number(1),
            ),
            parse("fight + 1"),
        )
    }

    @Test fun `parses subtraction`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.fight),
                operator = QueryBinaryOperator.SUBTRACT,
                right = number(1),
            ),
            parse("fight - 1"),
        )
    }

    @Test fun `parses multiplication`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.fight),
                operator = QueryBinaryOperator.MULTIPLY,
                right = number(2),
            ),
            parse("fight * 2"),
        )
    }

    @Test fun `parses division`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.fight),
                operator = QueryBinaryOperator.DIVIDE,
                right = number(2),
            ),
            parse("fight / 2"),
        )
    }

    @Test fun `parses modulo`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.fight),
                operator = QueryBinaryOperator.MODULO,
                right = number(2),
            ),
            parse("fight % 2"),
        )
    }

    // -------------------------------------------------------------------------
    // Binary precedence
    // -------------------------------------------------------------------------

    @Test
    fun `multiplication has higher precedence than addition`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.fight),
                operator = QueryBinaryOperator.ADD,
                right = QueryExpression.Binary(
                    left = field(QueryFields.evade),
                    operator = QueryBinaryOperator.MULTIPLY,
                    right = number(2),
                ),
            ),
            parse("fight + evade * 2"),
        )
    }

    @Test
    fun `division has higher precedence than subtraction`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.fight),
                operator = QueryBinaryOperator.SUBTRACT,
                right = QueryExpression.Binary(
                    left = field(QueryFields.evade),
                    operator = QueryBinaryOperator.DIVIDE,
                    right = number(2),
                ),
            ),
            parse("fight - evade / 2"),
        )
    }

    @Test
    fun `parses and`() {
        assertEquals(
            QueryExpression.Binary(
                left = parse("health > 5"),
                operator = QueryBinaryOperator.AND,
                right = parse("fight > 2"),
            ),
            parse("health > 5 & fight > 2"),
        )
    }

    @Test fun `parses or`() {
        assertEquals(
            QueryExpression.Binary(
                left = parse("health > 5"),
                operator = QueryBinaryOperator.OR,
                right = parse("fight > 2"),
            ),
            parse("health > 5 | fight > 2"),
        )
    }

    @Test fun `and has higher precedence than or`() {
        assertEquals(
            QueryExpression.Binary(
                left = parse("health > 5"),
                operator = QueryBinaryOperator.OR,
                right = QueryExpression.Binary(
                    left = parse("fight > 2"),
                    operator = QueryBinaryOperator.AND,
                    right = parse("evade > 2"),
                ),
            ),
            parse("health > 5 | fight > 2 & evade > 2"),
        )
    }

    @Test
    fun `Binary operators of same precedence are left associative`() {
        assertEquals(
            QueryExpression.Binary(
                left = QueryExpression.Binary(
                    left = parse("health > 5"),
                    operator = QueryBinaryOperator.AND,
                    right = parse("fight > 2"),
                ),
                operator = QueryBinaryOperator.AND,
                right = parse("evade > 2"),
            ),
            parse("health > 5 & fight > 2 & evade > 2"),
        )
    }

    // -------------------------------------------------------------------------
    // Parentheses
    // -------------------------------------------------------------------------

    @Test fun `parentheses override Binary precedence`() {
        assertEquals(
            QueryExpression.Binary(
                left = QueryExpression.Binary(
                    left = field(QueryFields.fight),
                    operator = QueryBinaryOperator.ADD,
                    right = number(1),
                ),
                operator = QueryBinaryOperator.MULTIPLY,
                right = number(2),
            ),
            parse("(fight + 1) * 2"),
        )
    }

    @Test fun `nested parentheses are supported`() {
        assertEquals(
            parse("health > 5"),
            parse("(((health > 5)))"),
        )
    }

    // -------------------------------------------------------------------------
    // Lists
    // -------------------------------------------------------------------------

    @Test
    fun `parses empty list`() {
        assertEquals(
            list(),
            parse("[]"),
        )
    }

    @Test
    fun `parses list of strings`() {
        assertEquals(
            list( string("fight"), string("evade")),
            parse("""["fight", "evade"]"""),
        )
    }

    @Test fun `parses list of numbers`() {
        assertEquals(
            list( number(1), number(2), number(3)),
            parse("[1, 2, 3]"),
        )
    }

    @Test fun `parses list of mixed literals`() {
        assertEquals(
            list( string("fight"), number(2), boolean(true), nullValue),
            parse("""["fight", 2, true, null]"""),
        )
    }

    @Test fun `parses expression in list`() {
        assertEquals(
            list(
                QueryExpression.Binary(
                    left = field(QueryFields.fight),
                    operator = QueryBinaryOperator.ADD,
                    right = number(1),
                ),
            ),
            parse("[fight + 1]"),
        )
    }

    @Test
    fun `parses multiple expressions in list`() {
        assertEquals(
            list(
                QueryExpression.Binary(
                    left = field(QueryFields.fight),
                    operator = QueryBinaryOperator.ADD,
                    right = number(1),
                ),
                QueryExpression.Binary(
                    left = field(QueryFields.evade),
                    operator = QueryBinaryOperator.ADD,
                    right = number(2),
                ),
            ),
            parse("[fight + 1, evade + 2]"),
        )
    }

    @Test
    fun `parses list expression used with contains`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.health),
                operator = QueryBinaryOperator.CONTAINS,
                right = list( QueryExpression.Binary(
                    left = field(QueryFields.fight),
                    operator = QueryBinaryOperator.ADD,
                    right = number(1),
                ), ),
            ),
            parse("health ? [fight + 1]"),
        )
    }

    @Test
    fun `parses complex expressions in list`() {
        assertEquals(
            list(
                QueryExpression.Binary(
                    left = QueryExpression.Binary(
                        left = field(QueryFields.fight),
                        operator = QueryBinaryOperator.ADD,
                        right = number(1),
                    ),
                    operator = QueryBinaryOperator.MULTIPLY,
                    right = number(2),
                ),
                QueryExpression.Binary(
                    left = field(QueryFields.evade),
                    operator = QueryBinaryOperator.SUBTRACT,
                    right = number(1),
                ),
            ),
            parse("[(fight + 1) * 2, evade - 1]"),
        )
    }

    // -------------------------------------------------------------------------
    // Lists with qualified fields
    // -------------------------------------------------------------------------

    @Test
    fun `parses real field expression in list`() {
        assertEquals(
            list(
                QueryExpression.Binary(
                    left = field( QueryFields.fight, real = true),
                    operator = QueryBinaryOperator.ADD,
                    right = number(1),
                ),
            ),
            parse("[real:fight + 1]"),
        )
    }

    @Test fun `parses real back field expression in list`() {
        assertEquals(
            list(
                QueryExpression.Binary(
                    left = field(QueryFields.text, real = true, back = true),
                    operator = QueryBinaryOperator.ADD,
                    right = string("test"),
                ),
            ),
        parse("""[real:back:text + "test"]"""),
        )
    }

    // -------------------------------------------------------------------------
    // Regex
    // -------------------------------------------------------------------------

    @Test
    fun `parses regex as Binary value`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.text),
                operator = QueryBinaryOperator.EQUALS,
                right = QueryExpression.Literal(
                    QueryValue.Regex("""fight\d+""")),
            ),
            parse("""text = /fight\d+/"""),
        )
    }

    @Test
    fun `parses division instead of regex outside value position`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.health),
                operator = QueryBinaryOperator.DIVIDE,
                right = number(2),
            ),
            parse("health / 2"),
        )
    }

    // -------------------------------------------------------------------------
    // Special numeric values
    // -------------------------------------------------------------------------

    @Test
    fun `parses dash special value as string`() {
        assertEquals(
            string("-"),
            parse("\"-\""),
        )
    }

    @Test fun `parses x special value as string`() {
        assertEquals(
            string("x"),
            parse("\"x\""),
        )
    }

    @Test fun `parses star special value as string`() {
        assertEquals(
            string("*"),
            parse("\"*\""),
        )
    }

    @Test fun `parses question special value as string`() {
        assertEquals(
            string("?"),
            parse("\"?\""),
        )
    }

    @Test fun `parses special value in Binary`() {
        assertEquals(
            QueryExpression.Binary(
                left = field(QueryFields.health),
                operator = QueryBinaryOperator.EQUALS,
                right = string("*"),
            ),
            parse("""health = "*" """.trim()),
        )
    }

    // -------------------------------------------------------------------------
    // Whitespace
    // -------------------------------------------------------------------------

    @Test
    fun `ignores whitespace`() {
        assertEquals(
            parse("health>=5&fight<2"),
            parse(" health >= 5 & fight < 2 "),
        )
    }

    @Test fun `newlines are treated as whitespace`() {
        assertEquals(
            parse("health > 5 & fight > 2"),
            parse(
                """
                    health > 5
                    &
                    fight > 2
                    """.trimIndent()
            ),
        )
    }

    // -------------------------------------------------------------------------
    // Errors
    // -------------------------------------------------------------------------

    @Test
    fun `fails on unknown field`() {
        assertThrows(QueryParserException::class.java) { parse("unknown = 1") }
    }

    @Test fun `fails on missing right operand`() {
        assertThrows(QueryParserException::class.java) { parse("health >") }
    }

    @Test fun `fails on missing closing parenthesis`() {
        assertThrows(QueryParserException::class.java) { parse("(health > 5") }
    }

    @Test fun `fails on unexpected closing parenthesis`() {
        assertThrows(QueryParserException::class.java) { parse("health > 5)") }
    }

    @Test fun `fails on duplicate real qualifier`() {
        assertThrows(QueryParserException::class.java) { parse("real:real:text") }
    }

    @Test fun `fails on duplicate back qualifier`() {
        assertThrows(QueryParserException::class.java) { parse("back:back:text") }
    }

    @Test fun `fails when field name is missing`() {
        assertThrows(QueryParserException::class.java) { parse("real:back:") }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun assertBinary(
        query: String,
        operator: QueryBinaryOperator,
        right: QueryExpression,
        left: QueryExpression = field(QueryFields.health),
    ) {
        assertEquals(
            QueryExpression.Binary(left, operator, right),
            parse(query),
        )
    }
}