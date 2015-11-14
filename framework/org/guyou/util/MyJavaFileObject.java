/**
 * 
 */
package org.guyou.util;

import java.io.IOException;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * @author zhusj
 *
 */
public class MyJavaFileObject extends SimpleJavaFileObject {
	private String code;
	public MyJavaFileObject(String name, String code) {
		super(URI.create("string:///" + name.replace('.', '/')
				+ Kind.SOURCE.extension), Kind.SOURCE);
		this.code = code;
	}
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors)
			throws IOException {
		return code;
	}
}
