package com.meidusa.fastbson.exception;

public class SerializeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SerializeException(String msg) {
		super(msg);
	}
	
	public SerializeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public static class GenericTypeNotDefinedException extends SerializeException {

		public GenericTypeNotDefinedException(String msg) {
			super(msg);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

	public static class WrongGenericTypeException extends SerializeException {

		public WrongGenericTypeException(String msg) {
			super(msg);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}
	
	public static class UnsupportedTypeException extends SerializeException {

		public UnsupportedTypeException(String msg) {
			super(msg);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	public static class ClassNotFoundException extends SerializeException {

		public ClassNotFoundException(String msg) {
			super(msg);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	public static class DuplicateFieldNameException extends SerializeException {

		public DuplicateFieldNameException(String msg) {
			super(msg);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	public static class ASMWrapperException extends SerializeException {

		public ASMWrapperException(String msg) {
			super(msg);
		}

		public ASMWrapperException(String msg, Throwable cause) {
			super(msg, cause);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	public static class EncodingException extends SerializeException {

		public EncodingException(String msg) {
			super(msg);
		}

		public EncodingException(String msg, Throwable cause) {
			super(msg, cause);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
	
	public static class ErrorPacketException extends SerializeException {

		public ErrorPacketException(String msg) {
			super(msg);
		}

		public ErrorPacketException(String msg, Throwable cause) {
			super(msg, cause);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
	}
}
