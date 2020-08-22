package mcenderdragon.nio.jarInjar.jmh;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.openjdk.jmh.infra.Blackhole;

public class BenchmarkBase 
{
	final URI jarFile;
	
	private List<String> sequential, random;
	
	public BenchmarkBase(URI jarFile) 
	{
		this.jarFile = jarFile;
	}
	
	public void setupFileLists()
	{
		setupFileSystem();
		try 
		{
			sequential = Utils.filesSequential(Paths.get(jarFile));
			random = Utils.filesRandomOrder(Paths.get(jarFile));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setupFileSystem()
	{
		try
		{
			FileSystems.getFileSystem(jarFile);
		}
		catch(FileSystemNotFoundException e)
		{
			try {
				FileSystems.newFileSystem(jarFile, Collections.emptyMap());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void cleanupFileSystem()
	{
		try 
		{
			FileSystems.getFileSystem(jarFile).close();
		}
		catch(FileSystemNotFoundException e) {} // already closed by another worker
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readSequential(Blackhole hole)
	{
		try {
			Utils.readEveryFile(FileSystems.getFileSystem(jarFile), sequential, hole);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readRandom(Blackhole hole)
	{
		try {
			Utils.readEveryFile(FileSystems.getFileSystem(jarFile), random, hole);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
