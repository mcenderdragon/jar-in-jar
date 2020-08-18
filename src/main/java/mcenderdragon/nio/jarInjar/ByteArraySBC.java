package mcenderdragon.nio.jarInjar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;

public class ByteArraySBC extends ReadOnlySBC 
{
	private final byte[] data;
	private int position = 0;
	
	public ByteArraySBC(byte[] data) 
	{
		super();
		this.data = data;
	}

	@Override
	public int read(ByteBuffer dst) throws IOException 
	{
		int len = Math.min(dst.remaining(),data.length - position);
		if(len> 0)
		{
			dst.put(data, position, len);
			position = data.length;
			return len;
		}
		else
		{
			return 0;
		}
	}

	@Override
	public long position() throws IOException 
	{
		return position;
	}

	@Override
	public SeekableByteChannel position(long newPosition) throws IOException 
	{
		position = (int) newPosition;
		return this;
	}

	@Override
	public long size() throws IOException 
	{
		return data.length;
	}

	@Override
	public boolean isOpen() 
	{
		return true;
	}

	@Override
	public void close() throws IOException 
	{
		
	}

}
