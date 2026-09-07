package com.arkhamcompanion.domain.arkhamql.lexer

import com.arkhamcompanion.domain.arkhamql.ast.QueryArithmeticOperator
import com.arkhamcompanion.domain.arkhamql.ast.QueryComparisonOperator
import com.arkhamcompanion.domain.arkhamql.ast.QueryLogicalOperator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class QueryLexerTest {

    @Test
    fun `tokenizes boolean true`() {
        assertEquals(
            listOf(
                QueryToken.BooleanLiteral(true, 0),
                QueryToken.End(4),
            ),
            QueryLexer("true").tokenize(),
        )
    }

    @Test
    fun `tokenizes boolean false`() {
        assertEquals(
            listOf(
                QueryToken.BooleanLiteral(false, 0),
                QueryToken.End(5),
            ),
            QueryLexer("false").tokenize(),
        )
    }

    @Test
    fun `tokenizes boolean keywords case-insensitively`() {
        assertEquals(
            listOf(
                QueryToken.BooleanLiteral(true, 0),
                QueryToken.End(4),
            ),
            QueryLexer("TRUE").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.BooleanLiteral(false, 0),
                QueryToken.End(5),
            ),
            QueryLexer("False").tokenize(),
        )
    }

    @Test
    fun `tokenizes null`() {
        assertEquals(
            listOf(
                QueryToken.NullLiteral(0),
                QueryToken.End(4),
            ),
            QueryLexer("null").tokenize(),
        )
    }

    @Test
    fun `tokenizes null case-insensitively`() {
        assertEquals(
            listOf(
                QueryToken.NullLiteral(0),
                QueryToken.End(4),
            ),
            QueryLexer("NULL").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.NullLiteral(0),
                QueryToken.End(4),
            ),
            QueryLexer("Null").tokenize(),
        )
    }

    @Test
    fun `tokenizes numbers`() {
        assertEquals(
            listOf(
                QueryToken.NumberLiteral(42, 0),
                QueryToken.End(2),
            ),
            QueryLexer("42").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.NumberLiteral(0, 0),
                QueryToken.End(1),
            ),
            QueryLexer("0").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.NumberLiteral(-5, 0),
                QueryToken.End(2),
            ),
            QueryLexer("-5").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.NumberLiteral(-0, 0),
                QueryToken.End(2),
            ),
            QueryLexer("-0").tokenize(),
        )
    }

    @Test
    fun `tokenizes strings`() {
        assertEquals(
            listOf(
                QueryToken.StringLiteral("hello world", 0),
                QueryToken.End(13),
            ),
            QueryLexer("\"hello world\"")
                .tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.StringLiteral("", 0),
                QueryToken.End(2),
            ),
            QueryLexer("\"\"").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.StringLiteral("hello\nworld", 0),
                QueryToken.End(14),
            ),
            QueryLexer("\"hello\\nworld\"")
                .tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.StringLiteral("say \"hello\"", 0),
                QueryToken.End(15),
            ),
            QueryLexer("\"say \\\"hello\\\"\"")
                .tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.StringLiteral("path\\to\\file", 0),
                QueryToken.End(16),
            ),
            QueryLexer("\"path\\\\to\\\\file\"")
                .tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.StringLiteral("hello world", 0),
                QueryToken.End(13),
            ),
            QueryLexer("\'hello world\'")
                .tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.StringLiteral("it\'s working", 0),
                QueryToken.End(15),
            ),
            QueryLexer("\'it\\\'s working\'")
                .tokenize(),
        )
    }

    @Test
    fun `tokenizes regex`() {
        assertEquals(
            listOf(
                QueryToken.RegexLiteral("test", 0),
                QueryToken.End(6),
            ),
            QueryLexer("/test/").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.RegexLiteral("\\d+ damage", 0),
                QueryToken.End(12),
            ),
            QueryLexer("/\\d+ damage/")
                .tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.RegexLiteral("test\\/path", 0),
                QueryToken.End(12),
            ),
            QueryLexer("/test\\/path/")
                .tokenize(),
        )
        assertThrows(
            QueryLexerException::class.java,
        ) { QueryLexer("/test").tokenize() }
    }

    @Test
    fun `tokenizes identifiers`() {
        assertEquals(
            listOf(
                QueryToken.Identifier("name", 0),
                QueryToken.End(4),
            ),
            QueryLexer("name").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.Identifier("xp", 0),
                QueryToken.End(2),
            ),
            QueryLexer("XP").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.Identifier("deck_limit", 0),
                QueryToken.End(10),
            ),
            QueryLexer("deck_limit").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.Identifier("field1", 0),
                QueryToken.End(6),
            ),
            QueryLexer("field1").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.Identifier("_private", 0),
                QueryToken.End(8),
            ),
            QueryLexer("_private").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.Identifier("健康", 0),
                QueryToken.Identifier("生命值", 3),
                QueryToken.Identifier("santé", 7),
                QueryToken.End(12),
            ),
            QueryLexer("健康 生命值 santé")
                .tokenize(),
        )
    }

    @Test
    fun `whitespace handling`() {
        assertEquals(
            listOf(
                QueryToken.BooleanLiteral(true, 2),
                QueryToken.End(8),
            ),
            QueryLexer("  true  ").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.BooleanLiteral(true, 1),
                QueryToken.End(6),
            ),
            QueryLexer("\ttrue\t").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.BooleanLiteral(true, 1),
                QueryToken.End(6),
            ),
            QueryLexer("\ntrue\n").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.End(0),
            ),
            QueryLexer("").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.End(7),
            ),
            QueryLexer("   \t\n  ").tokenize(),
        )
    }

    @Test
    fun `tokenizes complete expressions`() {
        assertEquals(
            listOf(
                QueryToken.Identifier("xp", 0),
                QueryToken.Operator(
                    QueryComparisonOperator.GREATER_OR_EQUAL, 3),
                QueryToken.NumberLiteral(3, 6),
                QueryToken.End(7),
            ),
            QueryLexer("xp >= 3").tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.Identifier("xp", 0),
                QueryToken.Operator(
                    QueryComparisonOperator.GREATER_THAN, 3),
                QueryToken.NumberLiteral(3, 5),
                QueryToken.Operator(
                    QueryLogicalOperator.AND, 7),
                QueryToken.Identifier("trait", 9),
                QueryToken.Operator(
                    QueryComparisonOperator.EQUALS, 15),
                QueryToken.StringLiteral("practiced", 17),
                QueryToken.End(28),
            ),
            QueryLexer("xp > 3 & trait = \"practiced\"")
                .tokenize(),
        )
        assertEquals(
            listOf(
                QueryToken.Identifier("trait", 0),
                QueryToken.Operator(
                    QueryComparisonOperator.EXACT_CONTAINS, 6),
                QueryToken.Operator(
                    GeneralSymbol.LEFT_BRACKET, 9),
                QueryToken.StringLiteral("Tactic.", 10),
                QueryToken.Operator(
                    GeneralSymbol.COMMA, 19),
                QueryToken.StringLiteral("Supply.", 21),
                QueryToken.Operator(
                    GeneralSymbol.RIGHT_BRACKET, 30),
                QueryToken.End(31),
            ),
            QueryLexer("trait ?? [\"Tactic.\", \"Supply.\"]")
                .tokenize(),
        )
    }

    @Test
    fun `tokenizes qualified field`() {
        assertEquals(
            listOf(
                QueryToken.Identifier("real:back:text", 0),
                QueryToken.End(14),
            ),
            QueryLexer("real:back:text")
                .tokenize(),
        )
    }

    @Test
    fun `tokenizes special numeric values as strings`() {
        assertEquals(
            listOf(
                QueryToken.Identifier("xp", 0),
                QueryToken.Operator(
                    QueryComparisonOperator.EQUALS, 3),
                QueryToken.StringLiteral("*", 5),
                QueryToken.End(8),
            ),
            QueryLexer("""xp = "*"""").tokenize(),
        )
    }

    @Test
    fun `tokenizes negative number`() {
        assertEquals(
            listOf(
                QueryToken.Identifier("xp", 0),
                QueryToken.Operator(
                    QueryComparisonOperator.EQUALS, 3),
                QueryToken.NumberLiteral(-3, 5),
                QueryToken.End(7),
            ),
            QueryLexer("xp = -3").tokenize(),
        )
    }

    @Test
    fun `distinguishes division from regex`() {
        assertEquals(
            listOf(
                QueryToken.Identifier("cost", 0),
                QueryToken.Operator(
                    QueryArithmeticOperator.DIVIDE, 5),
                QueryToken.NumberLiteral(2, 6),
                QueryToken.End(7),
            ),
            QueryLexer("cost /2").tokenize(),
        )
    }

    @Test
    fun `tokenizes regex after comparison`() {
        assertEquals(
            listOf(
                QueryToken.Identifier("text", 0),
                QueryToken.Operator(
                    QueryComparisonOperator.EQUALS, 5),
                QueryToken.RegexLiteral("""\d+ damage""", 7),
                QueryToken.End(19),
            ),
            QueryLexer("""text = /\d+ damage/""")
                .tokenize(),
        )
    }
}