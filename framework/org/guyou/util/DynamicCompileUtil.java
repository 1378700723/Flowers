/**
 * 
 */
package org.guyou.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * @author 朱施健 动态编译工具
 */
public class DynamicCompileUtil {

	private static DynamicEngine _dynamicEngine = null;
	
	public static DynamicEngine getDynamicEngine() throws UnsupportedEncodingException{
		if(_dynamicEngine==null){
			_dynamicEngine = new DynamicEngine();
		}
		return _dynamicEngine;
	}
	
	public static Class<?> stringToClass(String fullClassName,String javaCode) throws IllegalAccessException, InstantiationException, IOException{
		return getDynamicEngine().javaCodeToClass(fullClassName, javaCode);
	}
	
	public static Object stringToObject(String fullClassName,String javaCode) throws InstantiationException, IllegalAccessException, IOException{
		return getDynamicEngine().javaCodeToObject(fullClassName, javaCode);
	}
	
	private static class JavaClassObject extends SimpleJavaFileObject {
		private ByteArrayOutputStream bos = new ByteArrayOutputStream();

		JavaClassObject(String name, JavaFileObject.Kind kind) {
			super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
		}

		byte[] getBytes() {
			return bos.toByteArray();
		}

		@Override
		public OutputStream openOutputStream() throws IOException {
			return bos;
		}
	}

	private static class DynamicClassLoader extends URLClassLoader {
		DynamicClassLoader(ClassLoader parent) {
			super(new URL[0], parent);
		}

		Class<?> findClassByClassName(String className) throws ClassNotFoundException {
			return this.findClass(className);
		}

		Class<?> loadClass(String fullName, JavaClassObject jco) {
			byte[] classData = jco.getBytes();
			return this.defineClass(fullName, classData, 0, classData.length);
		}
	}

	private static class ClassFileManager extends ForwardingJavaFileManager {
		private JavaClassObject jclassObject;

		ClassFileManager(StandardJavaFileManager standardManager) {
			super(standardManager);
		}

		JavaClassObject getJavaClassObject() {
			return jclassObject;
		}

		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
			jclassObject = new JavaClassObject(className, kind);
			return jclassObject;
		}
	}

	private static class CharSequenceJavaFileObject extends SimpleJavaFileObject {
		private CharSequence content;

		CharSequenceJavaFileObject(String className, CharSequence content) {
			super(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
			this.content = content;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return content;
		}
	}

	public static class DynamicEngine {
		private URLClassLoader parentClassLoader;
		private String classpath;
		private DynamicClassLoader dynamicClassLoader;

		DynamicEngine() throws UnsupportedEncodingException {
			this.parentClassLoader = (URLClassLoader) this.getClass().getClassLoader();
			this.dynamicClassLoader = new DynamicClassLoader(this.parentClassLoader);
			List<URLClassLoader> loaders = new ArrayList<URLClassLoader>();
			loaders.add(this.parentClassLoader);
			while(true){
				URLClassLoader tmpLoader = (URLClassLoader) loaders.get(loaders.size()-1).getParent();
				if(tmpLoader==null || tmpLoader.getClass().getName().contains("ExtClassLoader")){
					break;
				}
				loaders.add(tmpLoader);
			}
			StringBuilder sb = new StringBuilder();
			for (URLClassLoader urlClassLoader : loaders) {
				for (URL url : urlClassLoader.getURLs()) {
					String p = url.getFile();
					if(sb.indexOf(p)==-1){
						sb.append(p).append(File.pathSeparator);
					}
				}
			}
			this.classpath = URLDecoder.decode(sb.toString(), SystemUtil.DEFAULT_CHARSET.name());
			loaders.clear();
		}

		public Class<?> javaCodeToClass(String fullClassName, String javaCode) throws IllegalAccessException, InstantiationException, IOException {
			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(diagnostics, null, Charset.forName("UTF-8")));
			List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
			jfiles.add(new CharSequenceJavaFileObject(fullClassName, javaCode));

			List<String> options = new ArrayList<String>();
			options.add("-encoding");
			options.add("UTF-8");
			options.add("-classpath");
			options.add(this.classpath);

			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, jfiles);
			boolean success = task.call();

			if (success) {
				JavaClassObject jco = fileManager.getJavaClassObject();
				Class<?> clazz = this.dynamicClassLoader.loadClass(fullClassName, jco);
				return clazz;
			} else {
				String error = "";
				for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
					error = error + compilePrint(diagnostic);
				}
			}
			return null;
		}
		
		public Object javaCodeToObject(String fullClassName, String javaCode) throws InstantiationException, IllegalAccessException, IOException{
			return javaCodeToClass(fullClassName,javaCode).newInstance();
		}

		String compilePrint(Diagnostic<?> diagnostic) {
			System.out.println("Code:" + diagnostic.getCode());
			System.out.println("Kind:" + diagnostic.getKind());
			System.out.println("Position:" + diagnostic.getPosition());
			System.out.println("Start Position:" + diagnostic.getStartPosition());
			System.out.println("End Position:" + diagnostic.getEndPosition());
			System.out.println("Source:" + diagnostic.getSource());
			System.out.println("Message:" + diagnostic.getMessage(null));
			System.out.println("LineNumber:" + diagnostic.getLineNumber());
			System.out.println("ColumnNumber:" + diagnostic.getColumnNumber());
			StringBuffer res = new StringBuffer();
			res.append("Code:[" + diagnostic.getCode() + "]\n");
			res.append("Kind:[" + diagnostic.getKind() + "]\n");
			res.append("Position:[" + diagnostic.getPosition() + "]\n");
			res.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
			res.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
			res.append("Source:[" + diagnostic.getSource() + "]\n");
			res.append("Message:[" + diagnostic.getMessage(null) + "]\n");
			res.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
			res.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
			return res.toString();
		}
	}
}
