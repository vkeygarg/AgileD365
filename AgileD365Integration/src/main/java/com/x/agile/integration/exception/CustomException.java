package com.x.agile.integration.exception;

public class CustomException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  
  public CustomException() {}
  
  public CustomException(String message, Throwable throwable)
  {
    super(message, throwable);
  }
}

/* Location:
 * Qualified Name:     com.x.agile.integration.exception.CustomException
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */