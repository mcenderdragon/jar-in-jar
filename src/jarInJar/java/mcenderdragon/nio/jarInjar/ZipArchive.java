package mcenderdragon.nio.jarInjar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipArchive 
{
	private final byte[] rawData;
	private final TreeMap<String, ZipEntry> name2entry;
	
	public static byte[] getContents(InputStream in) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream(in.available());
		byte[] buf = new byte[1024];
		while(in.available() > 0)
		{
			int wr = in.read(buf);
			out.write(buf, 0, wr);
		}
		in.close();
		out.close();
		return out.toByteArray();
	}
	
	public static ZipEntry[] getZipEntries(ZipInputStream zip) throws IOException
	{
		ArrayList<ZipEntry> list = new ArrayList();
		do
		{
			list.add(zip.getNextEntry());
		}
		while(list.get(list.size()-1) != null);
		if(list.get(list.size()-1) == null)
		{
			list.remove(list.size()-1);
		}
		
		return list.toArray(new ZipEntry[list.size()]);
	}
	
	public ZipArchive(InputStream in) throws IOException
	{
		this(getContents(in));
	}
	
	public ZipArchive(byte[] in) throws IOException
	{
		this.rawData = in;
		ByteArrayInputStream read = new ByteArrayInputStream(in);
		ZipInputStream zip = new ZipInputStream(read, StandardCharsets.UTF_8);
		ZipEntry[] entries = getZipEntries(zip);
		
		name2entry = new TreeMap<String, ZipEntry>();
		
		for(ZipEntry e : entries)
		{
			name2entry.put(e.getName(), e);
			System.out.println(e.getName());
		}
		
	}
	
	
	
}
