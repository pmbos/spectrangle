package model.test;

import static org.junit.jupiter.api.Assertions.*;
import model.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FieldTest {

	Field field;
	@BeforeEach
	void setUp() {
		field = new Field();
		
	}

	@Test
	void testInitialValues() {
		assertEquals(field.value(), 0);
		assertEquals(field.getColourL(), '\u0000');
		assertEquals(field.getColourV(), '\u0000');
		assertEquals(field.getColourR(), '\u0000');
	}
	
	@Test
	void testSetValue() {
		field.setValue(10);
		assertEquals(field.value(), 10);
	}
	
	@Test
	void testSetColourV() {
		field.setColourV('Y');
		assertEquals(field.getColourV(), 'Y');
	}

	@Test
	void testSetColourR() {
		field.setColourR('R');
		assertEquals(field.getColourR(), 'R');
	}
	
	@Test
	void testSetColourL() {
		field.setColourL('B');
		assertEquals(field.getColourL(), 'B');
	}
	
	@Test
	void testString() {
		System.out.println(field.toString());
	}
}
