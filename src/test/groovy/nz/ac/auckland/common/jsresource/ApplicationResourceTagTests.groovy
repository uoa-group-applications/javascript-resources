package nz.ac.auckland.common.jsresource

import nz.ac.auckland.lmz.common.AppVersion
import org.junit.Test

import javax.servlet.ServletContext
import javax.servlet.jsp.JspWriter
import javax.servlet.jsp.PageContext

/**
 *
 * @author: Richard Vowles - https://plus.google.com/+RichardVowles
 */
class ApplicationResourceTagTests {

	class MyWriter extends JspWriter {

		/**
		 * Protected constructor.
		 *
		 * @param bufferSize the size of the buffer to be used by the JspWriter
		 * @param autoFlush whether the JspWriter should be autoflushing
		 */
		private final Writer out

		protected MyWriter(Writer out) {
			super(100, true)
			this.out = out
		}

		@Override
		void newLine() throws IOException {

		}

		@Override
		void print(boolean b) throws IOException {

		}

		@Override
		void print(char c) throws IOException {

		}

		@Override
		void print(int i) throws IOException {

		}

		@Override
		void print(long l) throws IOException {

		}

		@Override
		void print(float f) throws IOException {

		}

		@Override
		void print(double d) throws IOException {

		}

		@Override
		void print(char[] s) throws IOException {

		}

		@Override
		void print(String s) throws IOException {
			out.print(s)
		}

		@Override
		void print(Object obj) throws IOException {

		}

		@Override
		void println() throws IOException {

		}

		@Override
		void println(boolean x) throws IOException {

		}

		@Override
		void println(char x) throws IOException {

		}

		@Override
		void println(int x) throws IOException {

		}

		@Override
		void println(long x) throws IOException {

		}

		@Override
		void println(float x) throws IOException {

		}

		@Override
		void println(double x) throws IOException {

		}

		@Override
		void println(char[] x) throws IOException {

		}

		@Override
		void println(String x) throws IOException {

		}

		@Override
		void println(Object x) throws IOException {

		}

		@Override
		void clear() throws IOException {

		}

		@Override
		void clearBuffer() throws IOException {

		}

		@Override
		void write(char[] cbuf, int off, int len) throws IOException {

		}

		@Override
		void flush() throws IOException {

		}

		@Override
		void close() throws IOException {

		}

		@Override
		int getRemaining() {
			return 0
		}
	}

	@Test
	public void ensureCorrectWritingOfJsp() {
		StringWriter writer = new StringWriter()

		ServletContext ctx = [
			 getContextPath: { return "/flarp" }
		] as ServletContext

		ApplicationResourceTag tag = new ApplicationResourceTag() {
			@Override
			protected void injectDependencies(ServletContext servletContext) {
				this.version = new AppVersion() {

					@Override
					String getVersion() {
						return "1.11"
					}
				}
			}
		}

		tag.setJspContext([getOut: {return new MyWriter(writer)}, getServletContext: { return ctx }] as PageContext)

		tag.doTag()

		String content = writer.toString()

		assert content == "<script src='/flarp/app-resources/1.11/global.js'></script><script src='/flarp/app-resources/1.11/session.js'></script>"
	}
}
