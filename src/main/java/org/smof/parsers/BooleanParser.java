/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.smof.parsers;

import org.bson.BsonValue;
import org.bson.codecs.configuration.CodecProvider;
import org.smof.bson.codecs.bool.SmofBooleanCodecProvider;
import org.smof.collection.SmofDispatcher;

@SuppressWarnings("javadoc")
public class BooleanParser extends AbstractBsonParser {

	static final CodecProvider PROVIDER = new SmofBooleanCodecProvider();
	private static final Class<?>[] VALID_TYPES = {Boolean.class, String.class, Integer.class};

	protected BooleanParser(SmofParser bsonParser, SmofDispatcher dispatcher) {
		super(dispatcher, bsonParser, PROVIDER, VALID_TYPES);
	}

	@Override
	public boolean isValidBson(BsonValue value) {
		return value.isBoolean() || super.isValidBson(value);
	}

}
