package tests.tests.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import com.kborowy.shortie.utils.UrlValidator

class UrlValidatorTest {

    @Test
    fun validatesUrls() {
        testCases.forEach { case ->
            assertEquals(
                case.valid,
                UrlValidator.validate(case.url),
                "${case.url} is not valid url",
            )
        }
    }
}

private data class TestCase(val url: String, val valid: Boolean)

private val testCases =
    listOf(
            // Valid URLs
            TestCase("http://example.com", true),
            TestCase("https://example.com", true),
            TestCase("https://www.example.com", true),
            TestCase("http://sub.domain.example.com", true),
            TestCase("https://example.com/path/to/resource", true),
            TestCase("https://example.com/?query=param", true),
            TestCase("https://example.com/#fragment", true),
            TestCase("https://example.com:8080", true),
            TestCase("ftp://example.com", true),
            TestCase("https://example.co.uk", true),
            TestCase("https://123.45.67.89", true),
            TestCase("https://localhost", true),
            TestCase("http://localhost:3000/test", true),
            TestCase("https://example.com/path?query=1&next=2", true),
            TestCase("https://xn--fsq.com", true), // punycode domain
            TestCase("https://user:pass@example.com", true),
            TestCase("https://my-site.com", true),

            // looks wrong, but handled by browsers just fine
            TestCase("https://example.com??test", true),
            TestCase("file:///etc/passwd", true),

            // looks good, but is not
            TestCase("https://例子.测试", false),

            // Invalid URLs
            TestCase("htp://example.com", false), // bad scheme
            TestCase("://example.com", false), // missing scheme
            TestCase("http:/example.com", false), // malformed scheme
            TestCase("http//example.com", false), // missing colon
            TestCase("example.com", false), // missing scheme
            TestCase("www.example.com", false), // missing scheme
            TestCase("https://", false), // missing host
            TestCase("https://example..com", false), // double dot
            TestCase("https://exa mple.com", false), // spaces in URL
            TestCase("https:///example.com", false), // too many slashes
            TestCase("https://example.com:abc", false), // invalid port
            TestCase("https://example .com", false), // space in domain
            TestCase("https://-example.com", false), // invalid leading hyphen
            TestCase("https://123.456.789.000", false), // invalid IP
            TestCase("", false), // empty string
            TestCase("   ", false), // whitespace only
            TestCase("not a url", false),
        )
        .shuffled()
