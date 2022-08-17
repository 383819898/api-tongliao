package com.shiku.mianshi.utils.sendpay.sdk.util;

import java.io.*;

public class Base64Util {
	private static char[] alphabet;
	private static byte[] codes;

	public static String encode(final String data) {
		return new String(encode(data.getBytes()));
	}

	public static byte[] decodeBytes(String data) {
		if (null == data) {
			return null;
		}
		data = data.replace(" ", "+");
		return decode(data.toCharArray());
	}

	public static String decode(String data) {
		try {
			data = data.replace(" ", "+");
			return new String(decode(data.toCharArray()), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static char[] encode(final byte[] data) {
		final char[] out = new char[(data.length + 2) / 3 * 4];
		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			boolean quad = false;
			boolean trip = false;
			int val = 0xFF & data[i];
			val <<= 8;
			if (i + 1 < data.length) {
				val |= (0xFF & data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if (i + 2 < data.length) {
				val |= (0xFF & data[i + 2]);
				quad = true;
			}
			out[index + 3] = Base64Util.alphabet[quad ? (val & 0x3F) : 64];
			val >>= 6;
			out[index + 2] = Base64Util.alphabet[trip ? (val & 0x3F) : 64];
			val >>= 6;
			out[index + 1] = Base64Util.alphabet[val & 0x3F];
			val >>= 6;
			out[index + 0] = Base64Util.alphabet[val & 0x3F];
		}
		return out;
	}

	public static byte[] decode(final char[] data) {
		int tempLen = data.length;
		for (int ix = 0; ix < data.length; ++ix) {
			if (data[ix] > '\u00ff' || Base64Util.codes[data[ix]] < 0) {
				--tempLen;
			}
		}
		int len = tempLen / 4 * 3;
		if (tempLen % 4 == 3) {
			len += 2;
		}
		if (tempLen % 4 == 2) {
			++len;
		}
		final byte[] out = new byte[len];
		int shift = 0;
		int accum = 0;
		int index = 0;
		for (int ix2 = 0; ix2 < data.length; ++ix2) {
			final int value = (data[ix2] > '\u00ff') ? -1 : Base64Util.codes[data[ix2]];
			if (value >= 0) {
				accum <<= 6;
				shift += 6;
				accum |= value;
				if (shift >= 8) {
					shift -= 8;
					out[index++] = (byte) (accum >> shift & 0xFF);
				}
			}
		}
		if (index != out.length) {
			throw new Error("Miscalculated data length (wrote " + index + " instead of " + out.length + ")");
		}
		return out;
	}

	public static void encode(File file) throws IOException {
		if (!file.exists()) {
			System.exit(0);
		} else {
			final byte[] decoded = readBytes(file);
			final char[] encoded = encode(decoded);
			writeChars(file, encoded);
		}
		file = null;
	}

	public static void decode(File file) throws IOException {
		if (!file.exists()) {
			System.exit(0);
		} else {
			final char[] encoded = readChars(file);
			final byte[] decoded = decode(encoded);
			writeBytes(file, decoded);
		}
		file = null;
	}

	private static byte[] readBytes(final File file) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] b = null;
		InputStream fis = null;
		InputStream is = null;
		try {
			fis = new FileInputStream(file);
			is = new BufferedInputStream(fis);
			int count = 0;
			final byte[] buf = new byte[16384];
			while ((count = is.read(buf)) != -1) {
				if (count > 0) {
					baos.write(buf, 0, count);
				}
			}
			b = baos.toByteArray();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (is != null) {
					is.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return b;
	}

	private static char[] readChars(final File file) throws IOException {
		final CharArrayWriter caw = new CharArrayWriter();
		Reader fr = null;
		Reader in = null;
		try {
			fr = new FileReader(file);
			in = new BufferedReader(fr);
			int count = 0;
			final char[] buf = new char[16384];
			while ((count = in.read(buf)) != -1) {
				if (count > 0) {
					caw.write(buf, 0, count);
				}
			}
		} finally {
			try {
				if (caw != null) {
					caw.close();
				}
				if (in != null) {
					in.close();
				}
				if (fr != null) {
					fr.close();
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return caw.toCharArray();
	}

	private static void writeBytes(final File file, final byte[] data) throws IOException {
		OutputStream fos = null;
		OutputStream os = null;
		try {
			fos = new FileOutputStream(file);
			os = new BufferedOutputStream(fos);
			os.write(data);
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private static void writeChars(final File file, final char[] data) throws IOException {
		Writer fos = null;
		Writer os = null;
		try {
			fos = new FileWriter(file);
			os = new BufferedWriter(fos);
			os.write(data);
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	static {
		Base64Util.alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
		Base64Util.codes = new byte[256];
		for (int i = 0; i < 256; ++i) {
			Base64Util.codes[i] = -1;
		}
		for (int i = 65; i <= 90; ++i) {
			Base64Util.codes[i] = (byte) (i - 65);
		}
		for (int i = 97; i <= 122; ++i) {
			Base64Util.codes[i] = (byte) (26 + i - 97);
		}
		for (int i = 48; i <= 57; ++i) {
			Base64Util.codes[i] = (byte) (52 + i - 48);
		}
		Base64Util.codes[43] = 62;
		Base64Util.codes[47] = 63;
	}
}
