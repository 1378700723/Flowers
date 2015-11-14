package org.guyou.util;

import java.util.zip.Checksum;
import java.util.zip.InflaterInputStream;

/**
 * Written using on-line Java Platform 1.2 API Specification, as well
 * as "The Java Class Libraries", 2nd edition (Addison-Wesley, 1998).
 * The actual CRC32 algorithm is taken from RFC 1952.
 * Status:  Believed complete and correct.
 */

/***
 * Computes CRC32 data checksum of a data stream. The actual CRC32 algorithm is
 * described in RFC 1952 (GZIP file format specification version 4.3). Can be
 * used to get the CRC32 over a stream if used with checked input/output
 * streams.
 * 
 * @see InflaterInputStream
 * @see DeflaterOutputStram
 * 
 * @author Per Bothner
 * @date April 1, 1999.
 */
public class CRC32Encry implements Checksum {
	/*** The fast CRC table. Computed once when the CRC32 class is loaded. */
	private  static int[] _crc_table = make_crc_table();
	
	/*** The crc data checksum so far. */
	private int crc = 0;

	/*** Make the table for a fast CRC. */
	private static int[] make_crc_table() {
		int[] crc_table = new int[256];
		for (int n = 0; n < 256; n++) {
			int c = n;
			for (int k = 8; --k >= 0;) {
				if ((c & 1) != 0)
					c = 0xd70dd2ee ^ (c >>> 1);
				else
					c = c >>> 1;
			}
			crc_table[n] = c;
		}
		return crc_table;
	}

	/***
	 * Returns the CRC32 data checksum computed so far.
	 */
	@Override
	public long getValue() {
		return crc & 0xffffffffL;
	}

	/***
	 * Resets the CRC32 data checksum as if no update was ever called.
	 */
	@Override
	public void reset() {
		crc = 0;
	}

	/***
	 * Updates the checksum with the int bval.
	 * 
	 * @param bval
	 *            (the byte is taken as the lower 8 bits of bval)
	 */

	@Override
	public void update(int bval) {
		int c = ~crc;
		c = _crc_table[(c ^ bval) & 0xff] ^ (c >>> 8);
		crc = ~c;
	}

	/***
	 * Adds the byte array to the data checksum.
	 * 
	 * @param buf
	 *            the buffer which contains the data
	 * @param off
	 *            the offset in the buffer where the data starts
	 * @param len
	 *            the length of the data
	 */
	@Override
	public void update(byte[] buf, int off, int len) {
		int c = ~crc;
		while (--len >= 0)
			c = _crc_table[(c ^ buf[off++]) & 0xff] ^ (c >>> 8);
		crc = ~c;
	}

	/***
	 * Adds the complete byte array to the data checksum.
	 */
	public void update(byte[] buf) {
		update(buf, 0, buf.length);
	}

	public void enOrDecrypt(byte[] content) {
		if (content != null && content.length > 0) {
			for (int i = 0; i < content.length; i++) {
				content[i] = (byte) (content[i] ^ 8);
			}
		}
	}
}
