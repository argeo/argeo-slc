package org.argeo.slc.maven.embedder;

/**
 * Logger sending everything to the standard output streams.
 * This is mainly for the cases when you have a utility that
 * does not have a logger to supply.
 *
 * @author <a href="mailto:dev@avalon.codehaus.org">Avalon Development Team</a>
 * @version $Id: MavenEmbedderConsoleLogger.java 292888 2005-10-01 01:17:36Z jvanzyl $
 */
public final class MavenEmbedderConsoleLogger
    extends AbstractMavenEmbedderLogger
{
    public void debug( String message, Throwable throwable )
    {
        if ( isDebugEnabled() )
        {
            System.out.print( "[ maven embedder DEBUG] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public void info( String message, Throwable throwable )
    {
        if ( isInfoEnabled() )
        {
            System.out.print( "[ maven embedder INFO] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public void warn( String message, Throwable throwable )
    {
        if ( isWarnEnabled() )
        {
            System.out.print( "[ maven embedder WARNING] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public void error( String message, Throwable throwable )
    {
        if ( isErrorEnabled() )
        {
            System.out.print( "[ maven embedder ERROR] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }

    public void fatalError( String message, Throwable throwable )
    {
        if ( isFatalErrorEnabled() )
        {
            System.out.print( "[ maven embedder FATAL ERROR] " );
            System.out.println( message );

            if ( null != throwable )
            {
                throwable.printStackTrace( System.out );
            }
        }
    }
}
