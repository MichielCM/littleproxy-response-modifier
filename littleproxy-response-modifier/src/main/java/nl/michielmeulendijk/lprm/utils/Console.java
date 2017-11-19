package nl.michielmeulendijk.lprm.utils;

import java.util.Arrays;

public final class Console {

	private final static int classNameLimit = 15;
	private final static int methodNameLimit = 30;
	
	public static void log(Object... objects) {
		System.out.println(
			String.format("%1$-".concat(String.valueOf(classNameLimit)).concat("s"),
				Thread.currentThread().getStackTrace()[2].getClassName().substring(
					Thread.currentThread().getStackTrace()[2].getClassName().lastIndexOf(".") + 1 
				)
			).replace(" ", ".").substring(0, classNameLimit).concat(
				" > "
			).concat(
				String.format("%1$-".concat(String.valueOf(methodNameLimit)).concat("s"),
					Thread.currentThread().getStackTrace()[2].getMethodName()
				).replace(" ", ".").substring(0, methodNameLimit)
			).concat(
				" #"
			).concat(
				String.format("%1$3s",
					String.valueOf(Thread.currentThread().getStackTrace()[2].getLineNumber())
				).replace(" ", "0")
			).concat(
				": "
			).concat(
				Arrays.toString(objects)
			)
		);
	}
	
}
