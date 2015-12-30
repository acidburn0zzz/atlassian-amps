import java.nio.charset.Charset

assert Charset.defaultCharset() != Charset.forName("UTF-16"), "Don't use UTF-16 as default charset"
