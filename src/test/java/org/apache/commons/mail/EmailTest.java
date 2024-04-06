package org.apache.commons.mail;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.Properties;
import javax.mail.Session;
import org.junit.*;

public class EmailTest {
	private static final String[] TEST_EMAILS = {"123@abc.com", "abc.ded@gf.net", "dffe@mdf.org"}; // array of fake emails
	private EmailConcrete email;
	
	@Before
	public void setUpEmailTest() throws Exception{
		email = new EmailConcrete();
		email.setSentDate(null); // used with getSentDate()
	}
	
	@After
	public void tearDownEmailTest() throws Exception{
		// left blank on purpose
	}
	
	// Tests for the 10 methods begin here
	// Tests for addBcc
	@Test
	public void testAddBcc_MultipleEmails() throws Exception{
		// testing a string of emails being added to bcc
		email.addBcc(TEST_EMAILS);
		
		assertEquals(3, email.getBccAddresses().size());
	}
	
	@Test
	public void testAddBcc_SingleEmail() throws Exception {
		// Testing adding a single bcc
		email.addBcc("abc.ef@fak.org");
		
		assertEquals(1, email.getBccAddresses().size());
	}
	
	@Test(expected = EmailException.class)
	public void testAddBcc_Empty() throws EmailException{
		// Attempting to add an empty bcc, will throw EmailException
		email.addBcc();
	}
	
	// Tests for addCc(String email)
	@Test
	public void testAddCc() throws Exception {
		// Adding array of emails to AddCc
		email.addCc(TEST_EMAILS);
		
		assertEquals(3, email.getCcAddresses().size());
	}
	
	@Test
	public void testAddCcSingleEmail() throws Exception{
		// Adding a single email to addCC
		String fakeEmail ="abcd@ef.com";
		email.addCc(fakeEmail);
		
		assertEquals(1, email.getCcAddresses().size());
	}
	
	@Test(expected = EmailException.class)
	public void testAddCc_EmptyEmailList() throws Exception{
		// Attempting to try and add no email, will throw EmailException
		email.addCc();
	}
	
	// Tests for addHeader(String name, String value)
	@Test
	public void testAddHeader() throws Exception {
		// Testing to see if addHeader adds correctly
		String Name = "BillyBob";
		String Value = "Important";
		
		email.addHeader(Name, Value);
		assertEquals(1, email.getHeaders().size());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddHeader_NullName() throws Exception {
		// expecting an IllegalArgumentException being thrown
		String value = "Important";
		email.addHeader(null, value);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddHeader_NullValue() throws Exception {
		// expecting an IllegalArgumentException being thrown
		String Name = "BillyBob";
		email.addHeader(Name, null);
	}
	
	// Tests for Email addReplyTo(String email, String name)
	@Test
	public void testAddReplyTo() throws Exception {
		String FakeEmail = "123.213@bcd.com";
		String Name = "BillyBob";
		email.addReplyTo(FakeEmail, Name);
		
		assertNotNull(email.replyList);
	}
	
	@Test
	public void testAddReplyTo_Singular() throws Exception {
		// Testing just one email address being added to addReplyTo
		String NotAnEmail = "1213@avc.com";
		email.addReplyTo(NotAnEmail);
		
		assertNotNull(email.replyList);
	}
	
	// Tests for Date getSentDate()
	@Test
	public void testGetSentDate() {
		// expecting getSentDate to not be null, default gets made in @before
		// when sentDate is null it automatically gets returned a new Date()
		assertNotNull(email.getSentDate());
	}
	
	@Test
	public void testGetSentDate_ValidSentDate() {
		// time timestamp should be the same as setting the sent date.
		long timestamp = System.currentTimeMillis();
		email.setSentDate(new Date(timestamp));
		
		assertEquals(timestamp, email.getSentDate().getTime());
	}
	
	// Test for int getSocketConnectionTimeout()
	@Test
	public void testGetSocketConnectionTimeout_Defualt() {
		// 60 seconds (default) to milliseconds is 60000
		assertEquals(60000, email.getSocketConnectionTimeout());
	}
	
	// Test for Email setFrom(String email)
	@Test
	public void testSetFrom_Default() throws Exception {
		String FakeEmail = "123.123@acc.com";
		email.setFrom(FakeEmail);
		
		assertNotNull(email.setFrom(FakeEmail));
	}
	
	// Tests for void buildMimeMessage()
	@Test
	public void testBuildMimeMessage_Duplicate() throws EmailException {
		// Not allowed to build duplicate messages so it should throw an exception
		try {
			email.setHostName("local");
			email.setSmtpPort(3000);
			email.setFrom("123.abc@abc.com");
			email.addTo("fake.Email@gbc.com");
			email.setSubject("Test Email");
			email.setCharset("ISO_8859_1");
			email.setContent(email, "test");
			
			email.buildMimeMessage();
			email.buildMimeMessage();
		} catch (IllegalStateException e) {
			assertEquals("The MimeMessage is already built.", e.getMessage());
		}
	}
	
	@Test
	public void testBuildMimeMessage_Success() throws EmailException {
		// testing build succeeds
		email.setHostName("local");
		email.setSmtpPort(2313);
		email.setFrom("123.abc@abc.com");
		email.addTo("fake.Email@gbc.com");
		email.setSubject("Test Email");
		email.setCharset("ISO_8859_1");
		email.setContent("TEXT_PLAIN", "This is test text");
		email.addCc("test.email@fc.com");
		email.addBcc("Another@ccc.com");
		email.setContent(email, "This should be body text?");
		
		email.buildMimeMessage();
		assertNotNull(email.getMimeMessage());
	}
	
	@Test 
	public void testBuildMimeMessage_No_From_Address() throws EmailException{
		// testing no From address branching
		try {
			email.setHostName("local");
			email.setSmtpPort(2313);
			email.addTo("fake.Email@gbc.com");
			email.setSubject("Test Email");
			email.setCharset("ISO_8859_1");
			email.setContent("TEXT_PLAIN", "This is test text");
		
			email.buildMimeMessage();
		} catch (EmailException e) {
			assertEquals("From address required",  e.getMessage());
		}
	}
	
	@Test
	public void testBuildMimeMessage_No_Receivers() throws EmailException{
		// testing no receivers branching
		try {
			email.setHostName("local");
			email.setSmtpPort(2313);
			email.setFrom("123.abc@abc.com");
			email.setSubject("Test Email");
			email.setCharset("ISO_8859_1");
			email.setContent("TEXT_PLAIN", "This is test text");
			
			email.buildMimeMessage();
		} catch (EmailException e) {
			assertEquals("At least one receiver address required", e.getMessage());
		}
	}
	
	@Test
	public void testBuildMimeMessage_addHeaders() throws Exception{
		// testing add headers branching
		email.setHostName("local");
		email.setSmtpPort(2313);
		email.setFrom("123.abc@abc.com");
		email.addTo("fake.Email@gbc.com");
		email.setSubject("Test Email");
		email.setCharset("ISO_8859_1");
		email.setContent("TEXT_PLAIN", "This is test text");
		email.addCc("test.email@fc.com");
		email.addBcc("Another@ccc.com");
		email.addHeader("header1", "value1");
		email.addHeader("header2", "value2");
		
        email.buildMimeMessage();
        assertNotNull(email.getMimeMessage().getAllHeaderLines());
	}
	
	@Test
	public void testBuildMimeMessage_NoCharSet() throws Exception{
		// Testing with no CharSet
		email.setHostName("local");
		email.setSmtpPort(2313);
		email.setFrom("123.abc@abc.com");
		email.addTo("fake.Email@gbc.com");
		email.setSubject("Test Email");
		email.addCc("test.email@fc.com");
		email.addBcc("Another@ccc.com");
		email.addHeader("header1", "value1");
		email.addHeader("header2", "value2");
		
        email.buildMimeMessage();
        assertNotNull(email.getMimeMessage()); // should build fine without setting charSet
	}
	
	// Tests for getHostName()
	@Test
	public void testGetHostName_SessionNotNull() throws Exception{
		Session session = Session.getInstance(new Properties());
		email.setHostName("testHost");
		int lengthHost = email.getHostName().length();
		
		assertNotNull(session);
		assertEquals(8, lengthHost);
	}
	
	@Test
	public void testGetHostName_EmptyHostName() throws Exception{
		Session session = Session.getInstance(new Properties());
		email.setHostName("");
		
		assertNotNull(session);
		assertNull(email.getHostName());
	}
	
	// Tests for getMailSession()
	@Test(expected = EmailException.class)
	public void testGetMailSession_NotSet() throws EmailException {
		// invoke without setting anything, should throw EmailException
		email.getMailSession();
	}
	
	@Test
	public void testGetMailSession_AuthenticationSet() throws EmailException {
		// Testing authentication branch
		email.setHostName("FakeHost.com");
		email.setSmtpPort(2332);
		email.setAuthenticator(new DefaultAuthenticator("username","password"));
		Session session = email.getMailSession();
		
		assertNotNull(session);
		assertEquals("FakeHost.com", session.getProperty("mail.smtp.host"));
		assertEquals("2332", session.getProperty("mail.smtp.port"));
		assertNotNull(session.getProperty("mail.smtp.auth"));
	}
	
	@Test // testing bounce back address being set
	public void testGetMailSession_BounceAddress() throws EmailException{
		email.setHostName("Fakehost.com");
		email.setBounceAddress("Thisisthebounce@fack.com");
		
		Session session = email.getMailSession();
		
		assertNotNull(session);
		assertEquals("Thisisthebounce@fack.com",session.getProperty("mail.smtp.from"));
	}
	
	@Test // testing if socket timeout is set right
	public void testGetMailSession_socketTimeout() throws EmailException {
		email.setHostName("Fakehost.com");
		email.setSocketTimeout(2000);
		Session session = email.getMailSession();
		
		assertNotNull(session);
		assertEquals("2000", session.getProperty("mail.smtp.timeout"));
	}
}
