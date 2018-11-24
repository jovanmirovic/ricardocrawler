package ch.ricardo.crawler.exception;

/**
 * <P>
 * Class for handling application exceptions.
 * <P/>
 * 
 * @author Jovan Mirovic
 * 
 *         Created 24 Nov 2018
 *
 */
public class ApplicationException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected String statusCode;

	protected Throwable cause;

	public ApplicationException(String message)
	{
		super(message);
	}

	public ApplicationException(String message, String statusCode)
	{
		super(message);
		this.statusCode = statusCode;
	}

	public ApplicationException(String message, Throwable cause)
	{
		this(message);
		this.cause = cause;
	}

	public ApplicationException(String message, String statusCode, Exception cause)
	{
		this(message, statusCode);
		this.cause = cause;
	}

	public String getStatusCode()
	{
		return statusCode;
	}

	@Override
	public Throwable getCause()
	{
		return cause;
	}

	public void setCause(Throwable cause)
	{
		this.cause = cause;
	}
}
