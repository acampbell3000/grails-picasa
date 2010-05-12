package uk.co.anthonycampbell.grails.taglib;

import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl

import org.springframework.context.support.ReloadableResourceBundleMessageSource as MessageSource
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.ObjectError

import groovy.lang.ExpandoMetaClass
import groovy.mock.interceptor.MockFor

class TextTagLibTests extends GroovyTestCase {
	def textTagLib;
	def testCSSName;
	def testYear;
	
	def testCSSTag;
	def testCopyrightText;
	
	void setUp(){
		testCSSName = "test.css"
		textTagLib = new TextTagLib()
		testYear = new GregorianCalendar().get(GregorianCalendar.YEAR)

		testCSSTag = """<link rel="stylesheet" href="/css/${testCSSName}" />"""
		testCopyrightText = """&copy; ${testYear}, All Rights Reserved"""
	}

    void testCopyright() {
		assertEquals("Unexpected copyright text returned!", testCopyrightText,
				textTagLib.copyright());
	}

    void testCopyright_Code() {
		def mockMessageSource = new MockFor(MessageSource.class)
		mockMessageSource.demand.getMessage{ a, b, c, d -> "Test copyright" }
		mockMessageSource.use {
			assertEquals("Unexpected copyright text returned!", "Test copyright",
					textTagLib.copyright([code: "copyright"]));
		}
	}

    void testCopyright_InvalidCode() {
		assertEquals("Unexpected copyright text returned!", testCopyrightText,
				textTagLib.copyright([code: "test"]));
	}

    void testCopyright_EmptyCode() {
		assertEquals("Unexpected copyright text returned!", testCopyrightText,
				textTagLib.copyright([code: ""]));
	}
	
	void testFieldError_NoBean() {
		try {
			textTagLib.fieldError([field: "test"]);
			fail("Tag [fieldError] should throw GrailsTagException when [bean] is NULL!");
		} catch (GrailsTagException GTE) {
			assertEquals("Unexpected GrailsTagException was thrown!",
					"Tag [fieldError] is missing required attribute [bean]", GTE.getMessage());
		}
	}
		
	void testFieldError_NoField() {
		try {
			textTagLib.fieldError([bean: "test"]);
			fail("Tag [fieldError] should throw GrailsTagException when [field] is NULL!");
		} catch (GrailsTagException GTE) {
			assertEquals("Unexpected GrailsTagException was thrown!",
					"Tag [fieldError] is missing required attribute [field]", GTE.getMessage());
		}
	}
	
	void testFieldError_NoBeanAndNoField() {
		try {
			textTagLib.fieldError();
			fail("Tag [fieldError] should throw GrailsTagException when [bean] is NULL!");
		} catch (GrailsTagException GTE) {
			assertEquals("Unexpected GrailsTagException was thrown!",
					"Tag [fieldError] is missing required attribute [bean]", GTE.getMessage());
		}
	}
	
	void testFieldError_EmptyBean() {
		def mockGroovySystem = new MockFor(GroovySystem.class)
		def mockMetaClassRegistry = new MockFor(MetaClassRegistryImpl.class)
		def mockExpandoMetaClass = new MockFor(ExpandoMetaClass.class)
		
		mockGroovySystem.demand.getMetaClassRegistry{ -> new MetaClassRegistryImpl()}
		mockMetaClassRegistry.demand.getMetaClass{ mcr -> new ExpandoMetaClass(String.class)}
		mockExpandoMetaClass.demand.hasProperty{ bean, errors -> false}
		
		mockGroovySystem.use {
			mockMetaClassRegistry.use {
				mockExpandoMetaClass.use {
					assertEquals("Unexpected field error text returned!", "",
							textTagLib.fieldError([bean: "", field: "test"]));
				}
			}
		}
	}
		
	void testFieldError_EmptyField() {	
		def mockGroovySystem = new MockFor(GroovySystem.class)
		def mockMetaClassRegistry = new MockFor(MetaClassRegistryImpl.class)
		def mockExpandoMetaClass = new MockFor(ExpandoMetaClass.class)
		
		mockGroovySystem.demand.getMetaClassRegistry{ -> new MetaClassRegistryImpl()}
		mockMetaClassRegistry.demand.getMetaClass{ mcr -> new ExpandoMetaClass(String.class)}
		mockExpandoMetaClass.demand.hasProperty{ bean, errors -> false}
		
		mockGroovySystem.use {
			mockMetaClassRegistry.use {
				mockExpandoMetaClass.use {
					assertEquals("Unexpected field error text returned!", "",
							textTagLib.fieldError([bean: "test", field: ""]));
				}
			}
		}
	}
		
	void testFieldError_EmptyBeanAndEmptyField() {
		def mockGroovySystem = new MockFor(GroovySystem.class)
		def mockMetaClassRegistry = new MockFor(MetaClassRegistryImpl.class)
		def mockExpandoMetaClass = new MockFor(ExpandoMetaClass.class)
		
		mockGroovySystem.demand.getMetaClassRegistry{ -> new MetaClassRegistryImpl()}
		mockMetaClassRegistry.demand.getMetaClass{ mcr -> new ExpandoMetaClass(String.class)}
		mockExpandoMetaClass.demand.hasProperty{ bean, errors -> false}
		
		mockGroovySystem.use {
			mockMetaClassRegistry.use {
				mockExpandoMetaClass.use {
					assertEquals("Unexpected field error text returned!", "",
							textTagLib.fieldError([bean: "", field: ""]));
				}
			}
		}
	}
		
	void testFieldError_NoErrors() {
		def mockGroovySystem = new MockFor(GroovySystem.class)
		def mockMetaClassRegistry = new MockFor(MetaClassRegistryImpl.class)
		def mockExpandoMetaClass = new MockFor(ExpandoMetaClass.class)
		
		mockGroovySystem.demand.getMetaClassRegistry{ -> new MetaClassRegistryImpl()}
		mockMetaClassRegistry.demand.getMetaClass{ mcr -> new ExpandoMetaClass(String.class)}
		mockExpandoMetaClass.demand.hasProperty{ bean, errors -> false}
		
		mockGroovySystem.use {
			mockMetaClassRegistry.use {
				mockExpandoMetaClass.use {
					assertEquals("Unexpected field error text returned!", "",
							textTagLib.fieldError([bean: "test", field: "test"]));
				}
			}
		}
	}
		
	void testFieldError_Errors() {
		def mockGroovySystem = new MockFor(GroovySystem.class)
		def mockMetaClassRegistry = new MockFor(MetaClassRegistryImpl.class)
		def mockExpandoMetaClass = new MockFor(ExpandoMetaClass.class)
		def mockFieldErrors = new MockFor(BeanPropertyBindingResult.class)
		def mockMessageSource = new MockFor(MessageSource.class)
		def mockTextTagLib = new MockFor(TextTagLib.class)
		def mockDomainClass = new MockFor(GrailsDefaultDomainClass.class)
		mockDomainClass.errors = new BeanPropertyBindingResult("Test", "Test")
		
		mockGroovySystem.demand.getMetaClassRegistry{ -> new MetaClassRegistryImpl() }
		mockMetaClassRegistry.demand.getMetaClass{ mcr -> new ExpandoMetaClass(String.class) }
		mockExpandoMetaClass.demand.hasProperty{ bean, propertyName -> true }
		mockFieldErrors.demand.hasErrors{ -> true }
		mockFieldErrors.demand.hasFieldErrors{ field -> true }
		mockFieldErrors.demand.getFieldError{ field -> new ObjectError("test", "test") }
		mockMessageSource.demand.getMessage{ a, b -> "Field error message" }
			
        mockGroovySystem.use {
			mockMetaClassRegistry.use {
				mockExpandoMetaClass.use {
					mockFieldErrors.use {
						mockMessageSource.use {
							assertEquals("Unexpected field error text returned!", "",
									textTagLib.fieldError([bean: mockDomainClass, field: "test"]));
						}
					}
				}
			}
		}
	}
}
