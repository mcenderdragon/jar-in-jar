package mcenderdragon.nio.jarInjar;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.ReadOnlyFileSystemException;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;
import java.util.regex.Pattern;

public abstract class AbstractReadOnlyFileSystem<T extends FileSystemProvider> extends FileSystem 
{
	protected final T provider;
	private boolean open = true;
	
	public AbstractReadOnlyFileSystem(T provider) 
	{
		this.provider = provider;
	}

	@Override
	public T provider() 
	{
		return provider;
	}

	@Override
	public void close() throws IOException 
	{
		open = false;
	}

	@Override
	public boolean isOpen() 
	{
		return open;
	}

	@Override
	public boolean isReadOnly() 
	{
		return true;
	}
	
	@Override
	public WatchService newWatchService() throws IOException 
	{
		throw new UnsupportedOperationException(new ReadOnlyFileSystemException()); 
	}
	
	private static final String GLOB_SYNTAX = "glob";
	private static final String REGEX_SYNTAX = "regex";
	
	@Override
	public PathMatcher getPathMatcher(String syntaxAndInput) 
	{
		int pos = syntaxAndInput.indexOf(':');
		if (pos <= 0 || pos == syntaxAndInput.length()) 
		{
			throw new IllegalArgumentException();
		}
		String syntax = syntaxAndInput.substring(0, pos);
		String input = syntaxAndInput.substring(pos + 1);
		String expr;
		if (syntax.equals(GLOB_SYNTAX)) 
		{
			expr = input.replaceAll("*", "[.]{0,}").replaceAll("?", "[.]{1}");
		} 
		else if (syntax.equals(REGEX_SYNTAX)) 
		{
			expr = input;
		}
		else
		{
			throw new UnsupportedOperationException("Syntax '" + syntax +"' not recognized");
		}
		// return matcher
		final Pattern pattern = Pattern.compile(expr);
		return new PathMatcher() 
		{
			@Override
			public boolean matches(Path path) 
			{
				return pattern.matcher(path.toString()).matches();
			}
		};
	}
}
