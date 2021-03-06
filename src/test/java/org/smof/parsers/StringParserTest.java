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

import org.bson.*;
import org.junit.Test;
import org.junit.Before;
import org.smof.dataModel.TypeGuitar;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by thales on 06/10/17.
 */
@SuppressWarnings("javadoc")
public class StringParserTest {
	
	private enum DummyEnum {
		VALUE1,
		VALUE2
	}
	
	private StringParser parser;
	
	@Before
	public void setUp() {
		parser = new StringParser(null, null);
	}

    @Test
    public void serializeToBson_ShouldSerialize_ObjectStringValueCorrectly() {
		final String testString = "some string";
        BsonValue bsonValue = parser.serializeToBson(testString, null);
        assertEquals(new BsonString(testString), bsonValue);
    }

    @Test
    public void serializeToBson_ShouldSerialize_ObjectEnumValueCorrectly() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        BsonValue bsonValue = abstractBsonParser.serializeToBson(TypeGuitar.ACOUSTIC, null);
        assertEquals("BsonString{value='ACOUSTIC'}", bsonValue.toString());
    }

    @Test
    public void serializeToBson_ShouldSerialize_ObjectIntegerValueCorrectly() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        BsonValue bsonValue = abstractBsonParser.serializeToBson(1, null);
        assertEquals("BsonString{value='1'}", bsonValue.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void serializeToBson_ShouldSerialize_ToNullAnd_RaiseException() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        abstractBsonParser.serializeToBson(BigDecimal.ONE, null);
    }

    @Test
    public void fromBson_ShouldReturn_StringRawValue() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        BsonString bsonString = new BsonString("bsonString");
        String string = abstractBsonParser.fromBson(bsonString, String.class, null);
        assertEquals("bsonString", string);
    }

    @Test
    public void fromBson_ShouldReturn_IntegerRawValue() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        BsonString bsonString = new BsonString("1");
        Integer integer = abstractBsonParser.fromBson(bsonString, Integer.class, null);
        assertEquals(1, integer.intValue());
    }
    
    @Test
    public void fromBson_ShouldReturn_EnumRawValue() {
    	final DummyEnum expected = DummyEnum.VALUE1;
		final BsonString bsonValue = new BsonString(expected.name());
		final DummyEnum actual = parser.fromBson(bsonValue, DummyEnum.class, null);
    	assertEquals(expected, actual);
    }

    @Test(expected = RuntimeException.class)
    public void fromBson_ShouldRaise_RuntimeException_IfValueIsNull() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        abstractBsonParser.fromBson(null, Boolean.class, null);
    }

    @Test
    public void isValidBson_ShouldReturn_True() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        boolean validBson = abstractBsonParser.isValidBson(new BsonString("a string"));
        assertTrue(validBson);
    }

    @Test
    public void isValidBson_ShouldReturn_False() {
        AbstractBsonParser abstractBsonParser = new StringParser(null, null);
        boolean validBson = abstractBsonParser.isValidBson(new BsonArray());
        assertFalse(validBson);
    }

}
