package mcenderdragon.nio.jarInjar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.SeekableByteChannel;

public abstract class ReadOnlySBC implements SeekableByteChannel 
{
	@Override
	public int write(ByteBuffer src) throws IOException 
	{
		throw new NonWritableChannelException();
	}

	@Override
	public SeekableByteChannel truncate(long size) throws IOException 
	{
		throw new NonWritableChannelException();
	}

}
