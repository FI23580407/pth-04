/*
 * ExtractFields plugin for Pentaho PTH-04
 * Copyright (C) 2021  Fail-Safe IT Solutions Oy
 *  
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *  
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *  
 *  
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *  
 * If you modify this Program, or any covered work, by linking or combining it 
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Fail-Safe IT Solutions Oy without any 
 * additional modifications.
 *  
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *  
 * Origin of the software must be attributed to Fail-Safe IT Solutions Oy. 
 * Any modified versions must be marked as "Modified version of" The Program.
 *  
 * Names of the licensors and authors may not be used for publicity purposes.
 *  
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *  
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *  
 */
package fi.failsafe.lms.pth04;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/*
 *  for alt implementation technology jni, see:
 *  https://plindenbaum.blogspot.com/2008/01/java-native-interface-jni-notebook.html
 */

public class LogNorm {
	public interface LibLogNorm extends Library {
		
		LibLogNorm INSTANCE = (LibLogNorm) 
				Native.load("/lib/opt/Fail-Safe/pth-04/lib/fslognorm/FSlognorm.so", LibLogNorm.class);
		
		public Pointer init(String repository);
		public Pointer normalize(Pointer ctx, String text);
		public String read_result(Pointer jref); // must be copied as destroy_result clears
		public void destroy_result(Pointer jref);
		public void deinit(Pointer ctx);
	}
	/*
	@SuppressWarnings("unused")
	private LogNorm() {
		// test
		String out;
		
		LogNorm logNorm = new LogNorm("rule=:%all:rest%");
		
		// a joke, does not really throw any exceptions from C code
		try {
			out = logNorm.Normalize("offline");	
		}
		finally {
			logNorm.Destroy();
		}
		
		System.out.println(out);
	}
	*/
	Pointer ctx;
	
	LogNorm(String rule) {
		
		this.ctx = LibLogNorm.INSTANCE.init(rule);
		
		if (this.ctx == Pointer.NULL) {
			 throw new NullPointerException("LogNorm() failed to initialize.");
		}
	}
	
	public String Normalize(String text) {
		if (this.ctx != Pointer.NULL) {
			Pointer jref = LibLogNorm.INSTANCE.normalize(ctx, text);

			if (jref == Pointer.NULL) {
				throw new NullPointerException("LogNorm() failed to perform extraction.");
			}

			String cstring = LibLogNorm.INSTANCE.read_result(jref);

			String javaString = String.copyValueOf(cstring.toCharArray(), 0, cstring.length());

			LibLogNorm.INSTANCE.destroy_result(jref);

			return javaString;
		}
		else {
			throw new IllegalArgumentException("LogNorm() not initialized.");
		}
	}
	
	public void Destroy() {
		if (this.ctx != Pointer.NULL) {
			LibLogNorm.INSTANCE.deinit(this.ctx);
		}
		else {
			throw new IllegalArgumentException("LogNorm() not initialized.");
		}
	}
}
