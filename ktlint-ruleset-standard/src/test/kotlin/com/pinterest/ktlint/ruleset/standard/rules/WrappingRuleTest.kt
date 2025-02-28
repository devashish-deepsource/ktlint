package com.pinterest.ktlint.ruleset.standard.rules

import com.pinterest.ktlint.rule.engine.core.api.editorconfig.INDENT_STYLE_PROPERTY
import com.pinterest.ktlint.test.KtLintAssertThat.Companion.EOL_CHAR
import com.pinterest.ktlint.test.KtLintAssertThat.Companion.MAX_LINE_LENGTH_MARKER
import com.pinterest.ktlint.test.KtLintAssertThat.Companion.assertThatRule
import com.pinterest.ktlint.test.LintViolation
import com.pinterest.ktlint.test.MULTILINE_STRING_QUOTE
import com.pinterest.ktlint.test.TAB
import org.ec4j.core.model.PropertyType.IndentStyleValue.tab
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class WrappingRuleTest {
    private val wrappingRuleAssertThat = assertThatRule { WrappingRule() }

    @Test
    fun `Given a multiline string containing a string-template as parameter value but then wrap the value to a start and end on separate lines`() {
        // Interpret "$." in code samples below as "$". It is used here as otherwise the indentation in the code sample
        // is disapproved when running ktlint on the unit tests during the build process (not that the indent rule can
        // not be disabled for a block).
        val code =
            """
            fun foo() {
                println("$.{
                true
                }")
            }
            """.trimIndent()
                .replacePlaceholderWithStringTemplate()
        val formattedCode =
            """
            fun foo() {
                println(
                    "$.{
                        true
                    }"
                )
            }
            """.trimIndent()
                .replacePlaceholderWithStringTemplate()
        wrappingRuleAssertThat(code)
            .addAdditionalRuleProvider { IndentationRule() }
            .hasLintViolations(
                LintViolation(2, 13, "Missing newline after \"(\""),
                LintViolation(4, 6, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Given a multiline raw string literal then wrap and indent conditionally`() {
        val code =
            """
            fun foo() {
                println($MULTILINE_STRING_QUOTE
                $MULTILINE_STRING_QUOTE)
                println($MULTILINE_STRING_QUOTE
                $MULTILINE_STRING_QUOTE.trimIndent())
                println($MULTILINE_STRING_QUOTE
                $MULTILINE_STRING_QUOTE.trimMargin())
            }
            """.trimIndent()
        val formattedCode =
            """
            fun foo() {
                println(
                    $MULTILINE_STRING_QUOTE
                $MULTILINE_STRING_QUOTE
                )
                println(
                    $MULTILINE_STRING_QUOTE
                    $MULTILINE_STRING_QUOTE.trimIndent()
                )
                println(
                    $MULTILINE_STRING_QUOTE
                    $MULTILINE_STRING_QUOTE.trimMargin()
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .addAdditionalRuleProvider { IndentationRule() }
            .hasLintViolations(
                LintViolation(2, 13, "Missing newline after \"(\""),
                LintViolation(3, 7, "Missing newline before \")\""),
                LintViolation(4, 13, "Missing newline after \"(\""),
                LintViolation(5, 20, "Missing newline before \")\""),
                LintViolation(6, 13, "Missing newline after \"(\""),
                LintViolation(7, 20, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Given some multiline raw string literal contain multiline string templates`() {
        val code =
            """
            fun foo1() {
                foo2($MULTILINE_STRING_QUOTE$.{
            true
                }
                text
            _$.{
            true
                }$MULTILINE_STRING_QUOTE.trimIndent(), ${MULTILINE_STRING_QUOTE}text$MULTILINE_STRING_QUOTE)
            }
            """.trimIndent()
                .replacePlaceholderWithStringTemplate()
        val formattedCode =
            """
            fun foo1() {
                foo2(
                    $MULTILINE_STRING_QUOTE$.{
                        true
                    }
                text
            _$.{
                        true
                    }
                    $MULTILINE_STRING_QUOTE.trimIndent(),
                    ${MULTILINE_STRING_QUOTE}text$MULTILINE_STRING_QUOTE
                )
            }
            """.trimIndent()
                .replacePlaceholderWithStringTemplate()
        wrappingRuleAssertThat(code)
            .addAdditionalRuleProvider { IndentationRule() }
            .hasLintViolations(
                LintViolation(2, 10, "Missing newline after \"(\""),
                LintViolation(8, 6, "Missing newline before \"\"\""),
                LintViolation(8, 23, "Missing newline after \",\""),
                LintViolation(8, 33, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Given a multiline raw string literal as function call parameter but not starting and ending on a separate line`() {
        val code =
            """
            fun foo() {
            println($MULTILINE_STRING_QUOTE
                text

                    text
            $MULTILINE_STRING_QUOTE.trimIndent().toByteArray())
            }
            """.trimIndent()
        val formattedCode =
            """
            fun foo() {
                println(
                    $MULTILINE_STRING_QUOTE
                text

                    text
                    $MULTILINE_STRING_QUOTE.trimIndent().toByteArray()
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .addAdditionalRuleProvider { IndentationRule() }
            .hasLintViolations(
                LintViolation(2, 9, "Missing newline after \"(\""),
                LintViolation(6, 30, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Given a multiline raw string literal as non-first function call parameter`() {
        val code =
            """
            fun foo() {
                write(fs.getPath("/projects/.editorconfig"), $MULTILINE_STRING_QUOTE
                    root = true
                    [*]
                    end_of_line = lf
                $MULTILINE_STRING_QUOTE.trimIndent().toByteArray())
            }
            """.trimIndent()
        val formattedCode =
            """
            fun foo() {
                write(
                    fs.getPath("/projects/.editorconfig"),
                    $MULTILINE_STRING_QUOTE
                    root = true
                    [*]
                    end_of_line = lf
                    $MULTILINE_STRING_QUOTE.trimIndent().toByteArray()
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .addAdditionalRuleProvider { IndentationRule() }
            .hasLintViolations(
                LintViolation(2, 11, "Missing newline after \"(\""),
                LintViolation(2, 49, "Missing newline after \",\""),
                LintViolation(6, 34, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Given some parameter lists`() {
        val code =
            """
            class C (val a: Int, val b: Int, val e: (
            r: Int
            ) -> Unit, val c: Int, val d: Int) {
            fun f(a: Int, b: Int, e: (
            r: Int
            ) -> Unit, c: Int, d: Int) {}
            }
            """.trimIndent()
        val formattedCode =
            """
            class C (
                val a: Int, val b: Int,
                val e: (
                    r: Int
                ) -> Unit,
                val c: Int, val d: Int
            ) {
                fun f(
                    a: Int, b: Int,
                    e: (
                        r: Int
                    ) -> Unit,
                    c: Int, d: Int
                ) {}
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .addAdditionalRuleProvider { IndentationRule() }
            .hasLintViolations(
                LintViolation(1, 10, "Missing newline after \"(\""),
                LintViolation(1, 33, "Missing newline after \",\""),
                LintViolation(3, 11, "Missing newline after \",\""),
                LintViolation(3, 33, "Missing newline before \")\""),
                LintViolation(4, 7, "Missing newline after \"(\""),
                LintViolation(4, 22, "Missing newline after \",\""),
                LintViolation(6, 11, "Missing newline after \",\""),
                LintViolation(6, 25, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Given a function call`() {
        val code =
            """
            fun main() {
                f(a, b, {
                // body
                }, c, d)

                fn(a,
                   b,
                   c)
            }
            """.trimIndent()
        val formattedCode =
            """
            fun main() {
                f(a, b, {
                    // body
                }, c, d)

                fn(
                    a,
                    b,
                    c
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .addAdditionalRuleProvider { IndentationRule() }
            .hasLintViolations(
                LintViolation(6, 8, "Missing newline after \"(\""),
                LintViolation(8, 8, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test // "https://github.com/shyiko/ktlint/issues/180"
    fun testLintWhereClause() {
        val code =
            """
            class BiAdapter<C : RecyclerView.ViewHolder, V1 : C, V2 : C, out A1, out A2>(
                val adapter1: A1,
                val adapter2: A2
            ) : RecyclerView.Adapter<C>()
                where A1 : RecyclerView.Adapter<V1>, A1 : ComposableAdapter.ViewTypeProvider,
                      A2 : RecyclerView.Adapter<V2>, A2 : ComposableAdapter.ViewTypeProvider {
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test // "https://github.com/pinterest/ktlint/issues/433"
    fun testLintParameterListWithComments() {
        val code =
            """
            fun main() {
                foo(
                    /*param1=*/param1,
                    /*param2=*/param2
                )

                foo(
                    /*param1=*/ param1,
                    /*param2=*/ param2
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `test wrapping rule allows line comment`() {
        val code =
            """
            interface Foo1 {}
            interface Foo2 {}
            interface Foo3 {}
            class Bar :
                Foo1, // this comment should be legal
                Foo2,// this comment should be legal
                Foo3 {
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `test wrapping rule allows block comment`() {
        val code =
            """
            interface Foo1 {}
            interface Foo2 {}
            interface Foo3 {}
            class Bar :
                Foo1, /* this comment should be legal */
                Foo2,/* this comment should be legal */
                Foo3 {
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun testLintNewlineAfterEqAllowed() {
        // Previously the IndentationRule would force the line break after the `=`. Verify that it is
        // still allowed.
        val code =
            """
            private fun getImplementationVersion() =
                javaClass.`package`.implementationVersion
                    ?: javaClass.getResourceAsStream("/META-INF/MANIFEST.MF")
                        ?.let { stream ->
                            Manifest(stream).mainAttributes.getValue("Implementation-Version")
                        }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint indentation new line before return type`() {
        val code =
            """
            abstract fun doPerformSomeOperation(param: ALongParameter):
                SomeLongInterface<ALongParameter.InnerClass, SomeOtherClass>
            val s:
                String = ""
            fun process(
                fileName:
                    String
            ): List<Output>
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint trailing comment in multiline parameter is allowed`() {
        val code =
            """
            fun foo(param: Foo, other: String) {
                foo(
                    param = param
                        .copy(foo = ""), // A comment
                    other = ""
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `format trailing comment in multiline parameter is allowed`() {
        val code =
            """
            fun foo(param: Foo, other: String) {
                foo(
                    param = param
                        .copy(foo = ""), // A comment
                    other = ""
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint safe-called wrapped trailing lambda is allowed`() {
        val code =
            """
            val foo = bar
                ?.filter { number ->
                    number == 0
                }?.map { evenNumber ->
                    evenNumber * evenNumber
                }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `format safe-called wrapped trailing lambda is allowed`() {
        val code =
            """
            val foo = bar
                ?.filter { number ->
                    number == 0
                }?.map { evenNumber ->
                    evenNumber * evenNumber
                }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint block started with parens after if is allowed`() {
        val code =
            """
            fun test() {
                if (true)
                    (1).toString()
                else
                    2.toString()
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `format block started with parens after if is allowed`() {
        val code =
            """
            fun test() {
                if (true)
                    (1).toString()
                else
                    2.toString()
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    // https://github.com/pinterest/ktlint/issues/796
    @Test
    fun `lint if-condition with multiline call expression is indented properly`() {
        val code =
            """
            private val gpsRegion =
                if (permissionHandler.isPermissionGranted(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    // stuff
                }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `format if-condition with multiline call expression is indented properly`() {
        val code =
            """
            private val gpsRegion =
                if (permissionHandler.isPermissionGranted(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    // stuff
                }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `format new line before opening quotes multiline string as parameter`() {
        val code =
            """
            fun foo() {
                println($MULTILINE_STRING_QUOTE
                    line1
                        line2
                    $MULTILINE_STRING_QUOTE.trimIndent())
            }
            """.trimIndent()
        val formattedCode =
            """
            fun foo() {
                println(
                    $MULTILINE_STRING_QUOTE
                    line1
                        line2
                    $MULTILINE_STRING_QUOTE.trimIndent()
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolations(
                LintViolation(2, 13, "Missing newline after \"(\""),
                LintViolation(5, 24, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    @Suppress("RemoveCurlyBracesFromTemplate")
    fun `format new line before opening quotes multiline string as parameter with tab spacing`() {
        val code =
            """
            fun foo() {
            ${TAB}println($MULTILINE_STRING_QUOTE
            ${TAB}${TAB}line1
            ${TAB}${TAB}    line2
            ${TAB}${TAB}$MULTILINE_STRING_QUOTE.trimIndent())
            }
            """.trimIndent()
        val formattedCode =
            """
            fun foo() {
            ${TAB}println(
            ${TAB}${TAB}$MULTILINE_STRING_QUOTE
            ${TAB}${TAB}line1
            ${TAB}${TAB}    line2
            ${TAB}${TAB}$MULTILINE_STRING_QUOTE.trimIndent()
            ${TAB})
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .withEditorConfigOverride(INDENT_STYLE_PROPERTY to tab)
            .hasLintViolations(
                LintViolation(2, 10, "Missing newline after \"(\""),
                LintViolation(5, 18, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `format multiline string containing quotation marks`() {
        val code =
            """
            fun foo() {
                println($MULTILINE_STRING_QUOTE
                    text ""

                         text
                         ""
                    $MULTILINE_STRING_QUOTE.trimIndent())
            }
            """.trimIndent()
        val formattedCode =
            """
            fun foo() {
                println(
                    $MULTILINE_STRING_QUOTE
                    text ""

                         text
                         ""
                    $MULTILINE_STRING_QUOTE.trimIndent()
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolations(
                LintViolation(2, 13, "Missing newline after \"(\""),
                LintViolation(7, 24, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `format multiline string containing a template string as the first non blank element on the line`() {
        // Escape '${true}' as '${"$"}{true}' to prevent evaluation before actually processing the multiline sting
        val code =
            """
            fun foo() {
                println($MULTILINE_STRING_QUOTE
                    ${"$"}{true}

                        ${"$"}{true}
                    $MULTILINE_STRING_QUOTE.trimIndent())
            }
            """.trimIndent()
        val formattedCode =
            """
            fun foo() {
                println(
                    $MULTILINE_STRING_QUOTE
                    ${"$"}{true}

                        ${"$"}{true}
                    $MULTILINE_STRING_QUOTE.trimIndent()
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolations(
                LintViolation(2, 13, "Missing newline after \"(\""),
                LintViolation(6, 24, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `issue 575 - format multiline string with tabs after the margin is indented properly`() {
        val code =
            """
            val str =
                $MULTILINE_STRING_QUOTE
                ${TAB}Tab at the beginning of this line but after the indentation margin
                Tab${TAB}in the middle of this string
                Tab at the end of this line.$TAB
                $MULTILINE_STRING_QUOTE.trimIndent()
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint if-condition with line break and multiline call expression is indented properly`() {
        val code =
            """
            // https://github.com/pinterest/ktlint/issues/871
            fun function(param1: Int, param2: Int, param3: Int?): Boolean {
                return if (
                    listOf(
                        param1,
                        param2,
                        param3
                    ).none { it != null }
                ) {
                    true
                } else {
                    false
                }
            }

            // https://github.com/pinterest/ktlint/issues/900
            enum class Letter(val value: String) {
                A("a"),
                B("b");
            }
            fun broken(key: String): Letter {
                for (letter in Letter.values()) {
                    if (
                        letter.value
                            .equals(
                                key,
                                ignoreCase = true
                            )
                    ) {
                        return letter
                    }
                }
                return Letter.B
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint property delegate is indented properly`() {
        val code =
            """
            val i: Int
                by lazy { 1 }

            val j = 0
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint property delegate is indented properly 2`() {
        val code =
            """
            val i: Int
                by lazy {
                    "".let {
                        println(it)
                    }
                    1
                }

            val j = 0
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint property delegate is indented properly 3`() {
        val code =
            """
            val i: Int by lazy {
                "".let {
                    println(it)
                }
                1
            }

            val j = 0
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint property delegate is indented properly 4`() {
        val code =
            """
            fun lazyList() = lazy { mutableListOf<String>() }

            class Test {
                val list: List<String>
                    by lazyList()

                val aVar = 0
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint property delegate is indented properly 5`() {
        val code =
            """
            fun lazyList(a: Int, b: Int) = lazy { mutableListOf<String>() }

            class Test {
                val list: List<String>
                    by lazyList(
                        1,
                        2
                    )

                val aVar = 0
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    // https://github.com/pinterest/ktlint/issues/1210
    @Test
    fun `lint delegated properties with a lambda argument`() {
        val code =
            """
            import kotlin.properties.Delegates

            class Test {
                private var test
                    by Delegates.vetoable("") { _, old, new ->
                        true
                    }
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint delegation 1`() {
        val code =
            """
            interface Foo

            class Bar(a: Int, b: Int, c: Int) : Foo

            class Test1 : Foo by Bar(
                a = 1,
                b = 2,
                c = 3
            )
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint and format delegation 2`() {
        val code =
            """
            class Test2 : Foo
            by Bar(
                a = 1,
                b = 2,
                c = 3
            )
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint delegation 3`() {
        val code =
            """
            interface Foo

            class Bar(a: Int, b: Int, c: Int) : Foo

            class Test3 :
                Foo by Bar(
                    a = 1,
                    b = 2,
                    c = 3
                )
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint delegation 4`() {
        val code =
            """
            interface Foo

            class Bar(a: Int, b: Int, c: Int) : Foo

            class Test4 :
                Foo
                by Bar(
                    a = 1,
                    b = 2,
                    c = 3
                )
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint delegation 5`() {
        val code =
            """
            interface Foo

            class Bar(a: Int, b: Int, c: Int) : Foo

            class Test5 {
                companion object : Foo by Bar(
                    a = 1,
                    b = 2,
                    c = 3
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint delegation 6`() {
        val code =
            """
            data class Shortcut(val id: String, val url: String)

            object Someclass : List<Shortcut> by listOf(
                Shortcut(
                    id = "1",
                    url = "url"
                ),
                Shortcut(
                    id = "2",
                    url = "asd"
                ),
                Shortcut(
                    id = "3",
                    url = "TV"
                )
            )
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint named argument`() {
        val code =
            """
            data class D(val a: Int, val b: Int, val c: Int)

            fun test() {
                val d = D(
                    a = 1,
                    b =
                    2,
                    c = 3
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint default parameter`() {
        val code =
            """
            data class D(
                val a: Int = 1,
                val b: Int =
                    2,
                val c: Int = 3
            )
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    // https://github.com/pinterest/ktlint/issues/959
    @Test
    fun `lint conditions with multi-line call expressions indented properly`() {
        val code =
            """
            fun test() {
                val result = true &&
                    minOf(
                        1, 2
                    ) == 2
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    // https://github.com/pinterest/ktlint/issues/1003
    @Test
    fun `lint multiple interfaces`() {
        val code =
            """
            abstract class Parent(a: Int, b: Int)

            interface Parent2

            class Child(
                a: Int,
                b: Int
            ) : Parent(
                a,
                b
            ),
                Parent2
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    // https://github.com/pinterest/ktlint/issues/918
    @Test
    fun `lint newline after type reference in functions`() {
        val code =
            """
            override fun actionProcessor():
                ObservableTransformer<in SomeVeryVeryLongNameOverHereAction, out SomeVeryVeryLongNameOverHereResult> =
                ObservableTransformer { actions ->
                    // ...
                }

            fun generateGooooooooooooooooogle():
                Gooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooogle {
                return Gooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooogle()
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    // https://github.com/pinterest/ktlint/issues/764
    @Test
    fun `lint value argument list with lambda`() {
        val code =
            """
            fun test(i: Int, f: (Int) -> Unit) {
                f(i)
            }

            fun main() {
                test(1, f = {
                    println(it)
                })
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint value argument list with two lambdas`() {
        val code =
            """
            fun test(f: () -> Unit, g: () -> Unit) {
                f()
                g()
            }

            fun main() {
                test({
                    println(1)
                }, {
                    println(2)
                })
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint value argument list with anonymous function`() {
        val code =
            """
            fun test(i: Int, f: (Int) -> Unit) {
                f(i)
            }

            fun main() {
                test(1, fun(it: Int) {
                    println(it)
                })
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `lint value argument list with lambda in super type entry`() {
        val code =
            """
            class A : B({
                1
            }) {
                val a = 1
            }

            open class B(f: () -> Int)
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    // https://github.com/pinterest/ktlint/issues/1202
    @Test
    fun `lint lambda argument and call chain`() {
        val code =
            """
            class Foo {
                fun bar() {
                    val foo = bar.associateBy({ item -> item.toString() }, ::someFunction).toMap()
                }
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    // https://github.com/pinterest/ktlint/issues/1165
    @Test
    fun `lint multiline expression with elvis operator in assignment`() {
        val code =
            """
            fun test() {
                val a: String = ""

                val someTest: Int?

                someTest =
                    a
                        .toIntOrNull()
                        ?: 1
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `multi line string at start of line`() {
        val code =
            """
            fun foo() =
            $MULTILINE_STRING_QUOTE
            some text
            $MULTILINE_STRING_QUOTE
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Given a multi line string but closing quotes not a separate line then wrap them to a new line`() {
        val code =
            """
            fun foo() =
                $MULTILINE_STRING_QUOTE
                some text$MULTILINE_STRING_QUOTE.trimIndent()
            """.trimIndent()
        val formattedCode =
            """
            fun foo() =
                $MULTILINE_STRING_QUOTE
                some text
                $MULTILINE_STRING_QUOTE.trimIndent()
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .addAdditionalRuleProvider { IndentationRule() }
            .hasLintViolation(3, 14, "Missing newline before \"\"\"")
            .isFormattedAs(formattedCode)
    }

    @Test
    fun `Issue 1127 - Given a raw string literal followed by trimIndent in parameter list`() {
        val code =
            """
            interface UserRepository : JpaRepository<User, UUID> {
                @Query($MULTILINE_STRING_QUOTE
                    select u from User u
                    inner join Organization o on u.organization = o
                    where o = :organization
                $MULTILINE_STRING_QUOTE.trimIndent())
                fun findByOrganization(organization: Organization, pageable: Pageable): Page<User>
            }
            """.trimIndent()
        val formattedCode =
            """
            interface UserRepository : JpaRepository<User, UUID> {
                @Query(
                    $MULTILINE_STRING_QUOTE
                    select u from User u
                    inner join Organization o on u.organization = o
                    where o = :organization
                    $MULTILINE_STRING_QUOTE.trimIndent()
                )
                fun findByOrganization(organization: Organization, pageable: Pageable): Page<User>
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .addAdditionalRuleProvider { IndentationRule() }
            .hasLintViolations(
                LintViolation(2, 12, "Missing newline after \"(\""),
                LintViolation(6, 20, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `format kdoc with spaces`() {
        val code =
            """
            /**
             * some function1
             */
            fun someFunction1() {
                return Unit
            }

            class SomeClass {
                /**
                 * some function2
                 */
                fun someFunction2() {
                    return Unit
                }
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()

        @Suppress("RemoveCurlyBracesFromTemplate")
        val codeTabs =
            """
            /**
             * some function1
             */
            fun someFunction1() {
            ${TAB}return Unit
            }

            class SomeClass {
            ${TAB}/**
            ${TAB} * some function2
            ${TAB} */
            ${TAB}fun someFunction2() {
            ${TAB}${TAB}return Unit
            ${TAB}}
            }
            """.trimIndent()
        wrappingRuleAssertThat(codeTabs)
            .withEditorConfigOverride(INDENT_STYLE_PROPERTY to tab)
            .hasNoLintViolations()
    }

    @Test
    fun `format kdoc with tabs`() {
        @Suppress("RemoveCurlyBracesFromTemplate")
        val code =
            """
            /**
             * some function1
             */
            fun someFunction1() {
            ${TAB}return Unit
            }

            class SomeClass {
            ${TAB}/**
            ${TAB} * some function2
            ${TAB} */
            ${TAB}fun someFunction2() {
            ${TAB}${TAB}return Unit
            ${TAB}}
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .withEditorConfigOverride(INDENT_STYLE_PROPERTY to tab)
            .hasNoLintViolations()
    }

    @Test
    fun `Issue 1210 - format supertype delegate`() {
        val code =
            """
            object ApplicationComponentFactory : ApplicationComponent.Factory
            by DaggerApplicationComponent.factory()
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Issue 1210 - format of statements after supertype delegated entry 2`() {
        val code =
            """
            interface Foo

            class Bar(a: Int, b: Int, c: Int) : Foo

            class Test4 :
                Foo
                by Bar(
                    a = 1,
                    b = 2,
                    c = 3
                )

            // The next line ensures that the fix regarding the expectedIndex due to alignment of "by" keyword in
            // class above, is still in place. Without this fix, the expectedIndex would hold a negative value,
            // resulting in the formatting to crash on the next line.
            val bar = 1
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Issue 1330 - Function with lambda parameter having a default value is allowed on a single line`() {
        val code =
            """
            fun func(lambdaArg: Unit.() -> Unit = {}, secondArg: Int) {
                println()
            }
            fun func(lambdaArg: Unit.(a: String) -> Unit = { it -> it.toUpperCaseAsciiOnly() }, secondArg: Int) {
                println()
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Function with multiple lambda parameters can be formatted differently`() {
        val code =
            """
            // https://github.com/pinterest/ktlint/issues/764#issuecomment-646822853
            val foo1 = println({
                bar()
            }, {
                bar()
            })
            // Other formats which should be allowed as well
            val foo2 = println(
                {
                    bar()
                },
                { bar() }
            )
            val foo3 = println(
                // Some comment
                {
                    bar()
                },
                // Some comment
                { bar() }
            )
            val foo4 = println(
                /* Some comment */
                {
                    bar()
                },
                /* Some comment */
                { bar() }
            )
            val foo5 = println(
                { bar() },
                { bar() }
            )
            val foo6 = println(
                // Some comment
                { bar() },
                // Some comment
                { bar() }
            )
            val foo7 = println(
                /* Some comment */
                { bar() },
                /* Some comment */
                { bar() }
            )
            val foo8 = println(
                { bar() }, { bar() }
            )
            val foo9 = println({ bar() }, { bar()})
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Given a class with one supertype with a multiline call entry then do not reformat`() {
        val code =
            """
            class FooBar : Foo({
            })
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Given a class for which all supertypes start on the same line but the last supertype has a multiline call entry then do not reformat`() {
        val code =
            """
            class FooBar : Foo1, Foo2({
            })
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Given a class with supertypes start on different lines then place each supertype on a separate line`() {
        val code =
            """
            class FooBar : Foo1, Foo2,
                Bar1, Bar2
            """.trimIndent()
        val formattedCode =
            """
            class FooBar :
                Foo1,
                Foo2,
                Bar1,
                Bar2
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolations(
                LintViolation(1, 15, "Missing newline after \":\""),
                LintViolation(1, 21, "Missing newline after \",\""),
                LintViolation(2, 10, "Missing newline after \",\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Given a class for which the supertypes start on a next line then do not reformat`() {
        val code =
            """
            class FooBar :
                Foo1, Foo2({
            })
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Given a class for which the supertypes start on a next line but they not all start on the same line then place each supertype on a separate line`() {
        val code =
            """
            class FooBar :
                Foo1, Foo2,
                Bar1, Bar2
            """.trimIndent()
        val formattedCode =
            """
            class FooBar :
                Foo1,
                Foo2,
                Bar1,
                Bar2
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolations(
                LintViolation(2, 10, "Missing newline after \",\""),
                LintViolation(3, 10, "Missing newline after \",\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Given a when condition with a multiline expression without block after the arrow then start that expression on the next line`() {
        val code =
            """
            val bar = when (foo) {
                1 -> true
                2 ->
                    false
                3 -> false ||
                    true
                4 -> false || foobar({
                }) // Special case which is allowed
                else -> {
                    true
                }
            }
            """.trimIndent()
        val formattedCode =
            """
            val bar = when (foo) {
                1 -> true
                2 ->
                    false
                3 ->
                    false ||
                    true
                4 -> false || foobar({
                }) // Special case which is allowed
                else -> {
                    true
                }
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolation(5, 8, "Missing newline after \"->\"")
            .isFormattedAs(formattedCode)
    }

    @Test
    fun `Given an multiline argument list which is incorrectly formatted then reformat `() {
        val code =
            """
            fun foo() =
                bar(a,
                    b,
                    c)
            """.trimIndent()
        val formattedCode =
            """
            fun foo() =
                bar(
                    a,
                    b,
                    c
                )
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolations(
                LintViolation(2, 9, "Missing newline after \"(\""),
                LintViolation(4, 9, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Given a function call and last parameter value is a function call then the clossing parenthesis may be on a single line`() {
        val code =
            """
            val foobar = foo(""
                + ""
                + bar("" // IDEA quirk (ignored)
                ))
            """.trimIndent()
        val formattedCode =
            """
            val foobar = foo(
                ""
                + ""
                + bar(
                    "" // IDEA quirk (ignored)
                )
            )
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolations(
                LintViolation(1, 18, "Missing newline after \"(\""),
                LintViolation(3, 11, "Missing newline after \"(\""),
                LintViolation(4, 5, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Multiline string starting at position 0`() {
        val code =
            """
            fun foo() {
                println($MULTILINE_STRING_QUOTE
                text

                    text
            _$MULTILINE_STRING_QUOTE.trimIndent())
            }
            """.trimIndent()
        val formattedCode =
            """
            fun foo() {
                println(
                    $MULTILINE_STRING_QUOTE
                text

                    text
            _
                    $MULTILINE_STRING_QUOTE.trimIndent()
                )
            }
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .addAdditionalRuleProvider { IndentationRule() }
            .hasLintViolations(
                LintViolation(2, 13, "Missing newline after \"(\""),
                LintViolation(6, 2, "Missing newline before \"\"\""),
                LintViolation(6, 17, "Missing newline before \")\""),
            ).isFormattedAs(formattedCode)
    }

    @Test
    fun `Issue 1375 - Do not wrap raw string literal when not followed by trimIndent or trimMargin`() {
        val code =
            """
            val someCodeBlock = $MULTILINE_STRING_QUOTE
              foo()$MULTILINE_STRING_QUOTE
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Issue 1375 - Wrap raw string literal when followed by trimIndent`() {
        val code =
            """
            val someCodeBlock = $MULTILINE_STRING_QUOTE
              foo()$MULTILINE_STRING_QUOTE.trimIndent()
            """.trimIndent()
        val formattedCode =
            """
            val someCodeBlock = $MULTILINE_STRING_QUOTE
              foo()
            $MULTILINE_STRING_QUOTE.trimIndent()
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolation(2, 8, "Missing newline before \"\"\"")
            .isFormattedAs(formattedCode)
    }

    @Test
    fun `Issue 1375 - Wrap raw string literal when followed by trimMargin`() {
        val code =
            """
            val someCodeBlock = $MULTILINE_STRING_QUOTE
              foo()$MULTILINE_STRING_QUOTE.trimMargin()
            """.trimIndent()
        val formattedCode =
            """
            val someCodeBlock = $MULTILINE_STRING_QUOTE
              foo()
            $MULTILINE_STRING_QUOTE.trimMargin()
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolation(2, 8, "Missing newline before \"\"\"")
            .isFormattedAs(formattedCode)
    }

    @Test
    fun `Issue 1350 - Given a for-statement with a newline in the expression only then do not wrap`() {
        val code =
            """
            fun foo() {
                for (item in listOf(
                    "a",
                    "b"
                )) {
                    println(item)
                }
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `Issue 1578 - Given a destructuring declaration followed by a trailing comma then do not require it to be followed by a newline as no other value parameter follows`() {
        val code =
            """
            // fun foo(block: (Pair<Int, Int>) -> Unit) {}
            val bar =
                foo {
                        (
                            a,
                            b,
                        ),
                    ->
                }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Nested
    inner class `Given a block starting and ending on the same line` {
        @Test
        fun `A single line block on a line which does not exceed the max line length is not wrapped`() {
            val code =
                """
                // $MAX_LINE_LENGTH_MARKER                                                   $EOL_CHAR
                class Bar {
                    val bar by lazy { foo("foooooooooooooooooooooooooooooooooooooooo", true) }
                }
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .setMaxLineLength()
                .hasNoLintViolations()
        }

        @Test
        fun `Issue 1643 - Wrap a block in case line including the block is violating the max line length`() {
            val code =
                """
                // $MAX_LINE_LENGTH_MARKER                                                   $EOL_CHAR
                class Bar {
                    val barrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr by lazy { fooooooooooooo("fooooooooooooooooooooooooooooooooooooooooooooo", true) }
                }
                """.trimIndent()
            val formattedCode =
                """
                // $MAX_LINE_LENGTH_MARKER                                                   $EOL_CHAR
                class Bar {
                    val barrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr by lazy {
                        fooooooooooooo("fooooooooooooooooooooooooooooooooooooooooooooo", true)
                    }
                }
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .setMaxLineLength()
                .hasLintViolation(3, 52, "Missing newline after \"{\"")
                .isFormattedAs(formattedCode)
        }

        @Test
        fun `Wrap a multiline block not containing a newline between the LBRACE and the first statement in that block even in case the line containing the start of the block does not exceed the max-line-length`() {
            val code =
                """
                class Bar {
                    val barrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr by lazy { "foo"
                    }
                }
                """.trimIndent()
            val formattedCode =
                """
                class Bar {
                    val barrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr by lazy {
                        "foo"
                    }
                }
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .hasLintViolation(2, 52, "Missing newline after \"{\"")
                .isFormattedAs(formattedCode)
        }

        @Test
        fun `Wrap a multiline block not containing a newline between the last statement in the block and the RBRACE even in case the line containing the end of the block does not exceed the max-line-length`() {
            val code =
                """
                class Bar {
                    val barrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr by lazy {
                        "foo" }
                }
                """.trimIndent()
            val formattedCode =
                """
                class Bar {
                    val barrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr by lazy {
                        "foo"
                    }
                }
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .hasLintViolation(3, 14, "Missing newline before \"}\"")
                .isFormattedAs(formattedCode)
        }

        @Test
        fun `Wrap a multiline block not containing whitespace elements before or after the block`() {
            val code =
                """
                class Bar {
                    val bar by lazy {${MULTILINE_STRING_QUOTE}foo
                            foo$MULTILINE_STRING_QUOTE}
                }
                """.trimIndent()
            val formattedCode =
                """
                class Bar {
                    val bar by lazy {
                        ${MULTILINE_STRING_QUOTE}foo
                            foo$MULTILINE_STRING_QUOTE
                    }
                }
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .hasLintViolations(
                    LintViolation(2, 22, "Missing newline after \"{\""),
                    LintViolation(3, 18, "Missing newline before \"}\""),
                ).isFormattedAs(formattedCode)
        }
    }

    @Test
    fun `Given a block with an EOL comment on the same line as the opening brace then the EOL comment should not be wrapped`() {
        val code =
            """
            val foo = fooBar { // some EOL comment
                bar()
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Nested
    inner class `Issue 1776 - Given a string template containing a block` {
        @Test
        fun `Given that the string template is on a separate line in the raw string literal`() {
            val code =
                """
                // $MAX_LINE_LENGTH_MARKER                        $EOL_CHAR
                fun getQueryString(query: QueryRequest): String {
                    val q = $MULTILINE_STRING_QUOTE
                        SELECT *
                        FROM table
                        WHERE 1 = 1
                        $.{query.gameId?.let { "AND id = ?" } ?: ""}
                    $MULTILINE_STRING_QUOTE
                    return q
                }
                """.replacePlaceholderWithStringTemplate()
                    .trimIndent()
            wrappingRuleAssertThat(code)
                .setMaxLineLength()
                .hasNoLintViolations()
        }

        @Test
        fun `Given that the string template is preceded by some text on the same line in the raw string literal`() {
            val code =
                """
                // $MAX_LINE_LENGTH_MARKER                               $EOL_CHAR
                fun getQueryString(query: QueryRequest): String {
                    val q = $MULTILINE_STRING_QUOTE
                        SELECT *
                        FROM table
                        WHERE $.{query.gameId?.let { "id = ?" } ?: "1 = 1"}
                    $MULTILINE_STRING_QUOTE
                    return q
                }
                """.replacePlaceholderWithStringTemplate()
                    .trimIndent()
            wrappingRuleAssertThat(code)
                .setMaxLineLength()
                .hasNoLintViolations()
        }

        @Test
        fun `Given that the string template is followed by some text on the same line in the raw string literal`() {
            val code =
                """
                // $MAX_LINE_LENGTH_MARKER                                            $EOL_CHAR
                fun getQueryString(query: QueryRequest): String {
                    val q = $MULTILINE_STRING_QUOTE
                        SELECT *
                        FROM table
                        WHERE $.{query.gameId?.let { "id = ?" } ?: "1 = 1"} OR level = ?
                    $MULTILINE_STRING_QUOTE
                    return q
                }
                """.replacePlaceholderWithStringTemplate()
                    .trimIndent()
            wrappingRuleAssertThat(code)
                .setMaxLineLength()
                .hasNoLintViolations()
        }
    }

    @Nested
    inner class `Given a multiline type argument list` {
        @Test
        fun `Given a single line type argument list then do not report a violation`() {
            val code =
                """
                val fooBar: FooBar<String, String> = emptyList()
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .hasNoLintViolations()
        }

        @Test
        fun `Given that newline is missing before some type projections`() {
            val code =
                """
                val fooBar: FooBar<Foo1, Foo2,
                    Bar1, Bar2,
                    > = emptyList()
                """.trimIndent()
            val formattedCode =
                """
                val fooBar: FooBar<
                    Foo1,
                    Foo2,
                    Bar1,
                    Bar2,
                    > = emptyList()
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .hasLintViolations(
                    LintViolation(1, 20, "A newline was expected before 'Foo1'"),
                    LintViolation(1, 26, "A newline was expected before 'Foo2'"),
                    LintViolation(2, 11, "A newline was expected before 'Bar2'"),
                ).isFormattedAs(formattedCode)
        }

        @Test
        fun `Given that newline is missing before the closing angle bracket`() {
            val code =
                """
                val fooBar: List<
                    Bar> = emptyList()
                """.trimIndent()
            val formattedCode =
                """
                val fooBar: List<
                    Bar
                    > = emptyList()
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .hasLintViolation(2, 8, "A newline was expected before '>'")
                .isFormattedAs(formattedCode)
        }

        @Test
        fun `Issue 1808 - Given a line including a block with exact length of line maximum`() {
            val code =
                """
                // $MAX_LINE_LENGTH_MARKER               $EOL_CHAR
                val foo = "fooooooooooooooo".map { "bar" }

                // Keep blank line above
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .setMaxLineLength()
                .hasNoLintViolations()
        }
    }

    @Test
    fun `Issue 1867 - Given a multiline type parameter list then wrap each type parameter to a new line`() {
        val code =
            """
            fun <
                Foo, Bar,
                FooBar,
                > foobar()
            """.trimIndent()
        val formattedCode =
            """
            fun <
                Foo,
                Bar,
                FooBar,
                > foobar()
            """.trimIndent()
        wrappingRuleAssertThat(code)
            .hasLintViolation(2, 10, "A newline was expected before 'Bar'")
            .isFormattedAs(formattedCode)
    }

    @Nested
    inner class `Issue 1078 - Given multiple expression seperated with semi in a single line` {
        @Nested
        inner class `Given multiple variables` {
            @Test
            fun `Given two variables`() {
                val code =
                    """
                    fun foo() {
                        val bar1 = 3; val bar2 = 2
                        val fooBar1: String = ""; val fooBar2: () -> Unit = {  }
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    fun foo() {
                        val bar1 = 3
                        val bar2 = 2
                        val fooBar1: String = ""
                        val fooBar2: () -> Unit = {  }
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(2, 18, "Missing newline after \";\""),
                        LintViolation(3, 30, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }

            @Test
            fun `Given two variables run without NoSemicolonsRule`() {
                val code =
                    """
                    fun foo() {
                        val bar1 = 3; val bar2 = 2
                        val fooBar1: String = ""; val fooBar2: () -> Unit = {  }
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    fun foo() {
                        val bar1 = 3;
                        val bar2 = 2
                        val fooBar1: String = "";
                        val fooBar2: () -> Unit = {  }
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .hasLintViolations(
                        LintViolation(2, 18, "Missing newline after \";\""),
                        LintViolation(3, 30, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }

            @Test
            fun `Given more than two variables`() {
                val code =
                    """
                    fun foo() {
                        val bar1 = 3; val bar2 = 2; val bar3 = 3; val bar4: () -> Unit = {  }; val bar4: String = "";
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    fun foo() {
                        val bar1 = 3
                        val bar2 = 2
                        val bar3 = 3
                        val bar4: () -> Unit = {  }
                        val bar4: String = ""
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(2, 18, "Missing newline after \";\""),
                        LintViolation(2, 32, "Missing newline after \";\""),
                        LintViolation(2, 46, "Missing newline after \";\""),
                        LintViolation(2, 75, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }

            @Test
            fun `Given variables with comments`() {
                val code =
                    """
                    fun foo() {
                        val bar1 = 3; val bar2 = 2; // this is end comment
                        val bar1 = 3; /* block comment */ val bar2 = 2;
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    fun foo() {
                        val bar1 = 3
                        val bar2 = 2; // this is end comment
                        val bar1 = 3
                        /* block comment */ val bar2 = 2
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(2, 18, "Missing newline after \";\""),
                        LintViolation(3, 18, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }
        }

        @Nested
        inner class `Given multiple classes, functions and init blocks` {
            @Test
            fun `Given multiple function declaration`() {
                val code =
                    """
                    public fun foo1() {
                        // no-op
                    }; public fun foo2() {
                        // no-op
                    }; fun foo3() = 0

                    public fun foo4() = 1; public fun foo5() {
                        // no-op
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    public fun foo1() {
                        // no-op
                    }
                    public fun foo2() {
                        // no-op
                    }
                    fun foo3() = 0

                    public fun foo4() = 1
                    public fun foo5() {
                        // no-op
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(3, 3, "Missing newline after \";\""),
                        LintViolation(5, 3, "Missing newline after \";\""),
                        LintViolation(7, 23, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }

            @Test
            fun `Given multiple function declaration with comments`() {
                val code =
                    """
                    public fun foo1() {
                        // no-op
                    }; /* block comment */ public fun foo2() {
                        // no-op
                    }; fun foo3() = 0 // single line comment
                    """.trimIndent()
                val formattedCode =
                    """
                    public fun foo1() {
                        // no-op
                    }
                    /* block comment */ public fun foo2() {
                        // no-op
                    }
                    fun foo3() = 0 // single line comment
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(3, 3, "Missing newline after \";\""),
                        LintViolation(5, 3, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }

            @Test
            fun `Given multiple function invocations`() {
                val code =
                    """
                    class Bar {
                        public fun foo1() = 0
                        fun foo2() = 0
                        fun foo3(lambda: () -> Unit) = 0

                        init {
                            foo1(); foo3 {  }; foo2()
                        }
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    class Bar {
                        public fun foo1() = 0
                        fun foo2() = 0
                        fun foo3(lambda: () -> Unit) = 0

                        init {
                            foo1()
                            foo3 {  }
                            foo2()
                        }
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(7, 16, "Missing newline after \";\""),
                        LintViolation(7, 27, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }

            @Test
            fun `Given a multiline class declaration`() {
                val code =
                    """
                    public class FooBar1 {

                    }; public class FooBar2 {

                    }

                    public class FooBar3; public class FooBar4
                    """.trimIndent()
                val formattedCode =
                    """
                    public class FooBar1 {

                    }
                    public class FooBar2 {

                    }

                    public class FooBar3
                    public class FooBar4
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(3, 3, "Missing newline after \";\""),
                        LintViolation(7, 22, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }

            @Test
            fun `Given a multiline class declaration with comments`() {
                val code =
                    """
                    public class FooBar1 {

                    }; /* block comment */ public class FooBar2 {

                    }; public class FooBar2 {

                    } // single line comment
                    """.trimIndent()
                val formattedCode =
                    """
                    public class FooBar1 {

                    }
                    /* block comment */ public class FooBar2 {

                    }
                    public class FooBar2 {

                    } // single line comment
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(3, 3, "Missing newline after \";\""),
                        LintViolation(5, 3, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }

            @Test
            fun `Given a multiple init block`() {
                val code =
                    """
                    public class Foo {
                        init {

                        };init {

                        }
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    public class Foo {
                        init {

                        }
                        init {

                        }
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolation(4, 7, "Missing newline after \";\"")
                    .isFormattedAs(formattedCode)
            }

            @Test
            fun `Given a multiple init block and variables with nested violations`() {
                val code =
                    """
                    public class Foo {
                        init {
                            val bar1 = 0; val bar2 = 0;
                        };init {
                            val bar3 = 0; val bar4 = 0;
                        }
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    public class Foo {
                        init {
                            val bar1 = 0
                            val bar2 = 0
                        }
                        init {
                            val bar3 = 0
                            val bar4 = 0
                        }
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(3, 22, "Missing newline after \";\""),
                        LintViolation(4, 7, "Missing newline after \";\""),
                        LintViolation(5, 22, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }
        }

        @Nested
        inner class `Given flow control statements` {
            @Test
            fun `Given a multiple for statements`() {
                val code =
                    """
                    fun test() {
                        for (i in 0..10) {
                            println(i)
                        }; for (i in 0..100) {
                            println(i)
                        }; for (i in 0..1000) {
                            println(i)
                        }
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    fun test() {
                        for (i in 0..10) {
                            println(i)
                        }
                        for (i in 0..100) {
                            println(i)
                        }
                        for (i in 0..1000) {
                            println(i)
                        }
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(4, 7, "Missing newline after \";\""),
                        LintViolation(6, 7, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }

            @Test
            fun `Given a multiline while statements`() {
                val code =
                    """
                    fun test() {
                        while (System.currentTimeMillis() % 2 == 0L) {
                            println(System.currentTimeMillis())
                        }; while (Random(System.currentTimeMillis()).nextBoolean()) {
                            println(System.currentTimeMillis())
                        }
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    fun test() {
                        while (System.currentTimeMillis() % 2 == 0L) {
                            println(System.currentTimeMillis())
                        }
                        while (Random(System.currentTimeMillis()).nextBoolean()) {
                            println(System.currentTimeMillis())
                        }
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolation(4, 7, "Missing newline after \";\"")
                    .isFormattedAs(formattedCode)
            }

            @Test
            fun `Given a multiline do-while statements`() {
                val code =
                    """
                    fun test() {
                        while (System.currentTimeMillis() % 2 == 0L) {
                            println(System.currentTimeMillis())
                        }; do {
                            println(System.currentTimeMillis())
                        } while (System.currentTimeMillis() % 2 == 0L); do {
                            println(System.currentTimeMillis())
                        } while (System.currentTimeMillis() % 2 == 0L)
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    fun test() {
                        while (System.currentTimeMillis() % 2 == 0L) {
                            println(System.currentTimeMillis())
                        }
                        do {
                            println(System.currentTimeMillis())
                        } while (System.currentTimeMillis() % 2 == 0L)
                        do {
                            println(System.currentTimeMillis())
                        } while (System.currentTimeMillis() % 2 == 0L)
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(4, 7, "Missing newline after \";\""),
                        LintViolation(6, 52, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }

            @Test
            fun `Given a multiline semi separated control flow with no body`() {
                val code =
                    """
                    fun test() {
                        for (i in 0..10); for (i in 0..100);while (System.currentTimeMillis() % 2 == 0L); while (Random(System.currentTimeMillis()).nextBoolean());
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    fun test() {
                        for (i in 0..10);
                        for (i in 0..100);
                        while (System.currentTimeMillis() % 2 == 0L);
                        while (Random(System.currentTimeMillis()).nextBoolean());
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolations(
                        LintViolation(2, 22, "Missing newline after \";\""),
                        LintViolation(2, 41, "Missing newline after \";\""),
                        LintViolation(2, 86, "Missing newline after \";\""),
                    ).isFormattedAs(formattedCode)
            }
        }

        @Test
        fun `Given a multiline semi separated import statement then wrap each expression to a new line`() {
            val code =
                """
                import java.util.ArrayList; import java.util.HashMap
                """.trimIndent()
            val formattedCode =
                """
                import java.util.ArrayList
                import java.util.HashMap
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .addAdditionalRuleProvider { NoSemicolonsRule() }
                .hasLintViolation(1, 28, "Missing newline after \";\"")
                .isFormattedAs(formattedCode)
        }

        @Test
        fun `Given a multiline semi separated with variables, flow controls and method calls`() {
            val code =
                """
                fun test() {
                    val a = 0; val b = 0; fun bar() {
                        // no-op
                    }; for(i in 0..10) {
                        println(i); println(i); a++; println(a)
                    }
                }
                """.trimIndent()
            val formattedCode =
                """
                fun test() {
                    val a = 0
                    val b = 0
                    fun bar() {
                        // no-op
                    }
                    for(i in 0..10) {
                        println(i)
                        println(i)
                        a++
                        println(a)
                    }
                }
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .addAdditionalRuleProvider { NoSemicolonsRule() }
                .hasLintViolations(
                    LintViolation(2, 15, "Missing newline after \";\""),
                    LintViolation(2, 26, "Missing newline after \";\""),
                    LintViolation(4, 7, "Missing newline after \";\""),
                    LintViolation(5, 20, "Missing newline after \";\""),
                    LintViolation(5, 32, "Missing newline after \";\""),
                    LintViolation(5, 37, "Missing newline after \";\""),
                ).isFormattedAs(formattedCode)
        }

        @Nested
        inner class `Given enum class` {
            @Test
            fun `Given a enum without ending semi`() {
                val code =
                    """
                    enum class FOO1 { ONE, TWO, THREE }
                    enum class FOO2 {
                        ONE,
                        TWO,
                        THREE
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .hasNoLintViolations()
            }

            @Test
            fun `Given a enum with ending semi`() {
                val code =
                    """
                    enum class FOO1 { ONE, TWO, THREE; }
                    enum class FOO2 {
                        ONE, TWO, THREE;
                    }
                    enum class FOO3 {
                        ONE,
                        TWO,
                        THREE;
                    }
                    enum class FOO4 {
                        ONE,
                        TWO,
                        THREE,
                        ;
                    }
                    enum class FOO5 {
                        ONE,
                        TWO,
                        THREE,
                        ;
                        fun foo() = ""
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .hasNoLintViolations()
            }

            @Test
            fun `Given a enum with ending semi with comment`() {
                val code =
                    """
                    enum class FOO1 { ONE, TWO, THREE; /* with comment */ }
                    enum class FOO2 {
                        ONE,
                        TWO,
                        THREE, // single line comment
                        ; // last single line comment
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .hasNoLintViolations()
            }

            @Test
            fun `Given enum class with methods`() {
                val code =
                    """
                    enum class FOO {
                        A, B, C; fun test() = 0
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    enum class FOO {
                        A, B, C;
                        fun test() = 0
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolation(2, 13, "Missing newline after \";\"")
                    .isFormattedAs(formattedCode)
            }
        }

        @Nested
        inner class `Given companion or object class` {
            @Test
            fun `Given a companion object with semicolon and variable`() {
                val code =
                    """
                    class Foo() {
                        companion object; private var toto: Boolean = false
                    }
                    """.trimIndent()
                val formattedCode =
                    """
                    class Foo() {
                        companion object;
                        private var toto: Boolean = false
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .addAdditionalRuleProvider { NoSemicolonsRule() }
                    .hasLintViolation(2, 22, "Missing newline after \";\"")
                    .isFormattedAs(formattedCode)
            }

            @Test
            fun `Given a companion object with semicolon and comment has not violation`() {
                val code =
                    """
                    class Foo() {
                        companion object; // single-line comment
                    }
                    """.trimIndent()
                wrappingRuleAssertThat(code)
                    .hasNoLintViolations()
            }
        }

        @Test
        fun `Given a single line block containing multiple statements then reformat block after wrapping the statement`() {
            val code =
                """
                val fooBar =
                    fooBar()
                        .map { foo(); bar() }
                """.trimIndent()
            val formattedCode =
                """
                val fooBar =
                    fooBar()
                        .map {
                            foo()
                            bar()
                        }
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .addAdditionalRuleProvider { NoSemicolonsRule() }
                .addAdditionalRuleProvider { IndentationRule() }
                .hasLintViolation(3, 22, "Missing newline after \";\"")
                .isFormattedAs(formattedCode)
        }

        @Test
        fun `Given a single line lambda expression containing multiple statements then reformat block after wrapping the statement`() {
            val code =
                """
                val fooBar =
                    fooBar()
                        .map { foo, bar -> print(foo); print(bar) }
                """.trimIndent()
            val formattedCode =
                """
                val fooBar =
                    fooBar()
                        .map { foo, bar ->
                            print(foo)
                            print(bar)
                        }
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .addAdditionalRuleProvider { NoSemicolonsRule() }
                .addAdditionalRuleProvider { IndentationRule() }
                .hasLintViolation(3, 39, "Missing newline after \";\"")
                .isFormattedAs(formattedCode)
        }

        @Test
        fun `Given a single line when entry block containing multiple statements then reformat block after wrapping the statement`() {
            val code =
                """
                val foo =
                    when (value) {
                        0 -> { foo(); true }
                        else -> { bar(); false }
                    }
                """.trimIndent()
            val formattedCode =
                """
                val foo =
                    when (value) {
                        0 -> {
                            foo()
                            true
                        }
                        else -> {
                            bar()
                            false
                        }
                    }
                """.trimIndent()
            wrappingRuleAssertThat(code)
                .addAdditionalRuleProvider { NoSemicolonsRule() }
                .addAdditionalRuleProvider { IndentationRule() }
                .hasLintViolations(
                    LintViolation(3, 22, "Missing newline after \";\""),
                    LintViolation(4, 25, "Missing newline after \";\""),
                ).isFormattedAs(formattedCode)
        }
    }

    @Test
    fun `Given a lambda expression containing a function literal`() {
        val code =
            """
            val foo = {
                Foo { "foo" }
            }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }

    @Test
    fun `if else with comments after curly braces`() {
        val code =
            """
            val foo =
                if (true) { // comment 1
                    "foo"
                } else { // comment 2
                    "bar"
                }
            """.trimIndent()
        wrappingRuleAssertThat(code).hasNoLintViolations()
    }
}

// Replace the "$." placeholder with an actual "$" so that string "$.{expression}" is transformed to a String template
// "${expression}".
private fun String.replacePlaceholderWithStringTemplate() = replace("$.", "${'$'}")
