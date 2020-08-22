package mcenderdragon.nio.jarInjar.jmh;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import mcenderdragon.nio.jarInjar.HelperURIFormatter;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class Benchmarks
{
	
	public static final URI jar;
	public static final URI jarInJar;
	static
	{
		File base = new File("./src/main/resources/test.jar").getAbsoluteFile();
		System.out.println(base);
		
		URI j;
		URI jij; 
		try
		{
			j = new URI("jar", base.toURI() + "!/", "");
			System.out.println(j);
			jij = HelperURIFormatter.asJarInJarURI(base);
			System.out.println(jij);
		}
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
			jij = null;
			j = null;
			System.exit(1);
		}
		jar = j;
		jarInJar = jij;
	}
	
	//open jar with jar in jar
	//open jar in jar (with jar in jar)
	//open jar in jar (with only jar)
	
	@State(Scope.Thread)
	public static class StateJAR extends BenchmarkBase
	{

		public StateJAR() 
		{
			super(jar);
		}
		
		@Setup(Level.Trial)
		@Override
		public void setupFileLists() 
		{
			super.setupFileLists();
		}
		
		@Setup(Level.Trial)
		@Override
		public void setupFileSystem() 
		{
			super.setupFileSystem();
		}
		
		@TearDown(Level.Trial)
		@Override
		public void cleanupFileSystem() {
			super.cleanupFileSystem();
		}
	}
	
	@Benchmark
	public static void benchJARinJARordered(StateJARinJAR state)
	{
		state.readSequential();
	}
	
	@Benchmark
	public static void benchJARinJARunordered(StateJARinJAR state)
	{
		state.readRandom();
	}
	
	@Benchmark
	public static void benchJARordered(StateJAR state)
	{
		state.readSequential();
	}
	
	@Benchmark
	public static void benchJARunordered(StateJAR state)
	{
		state.readRandom();
	}
	
	@State(Scope.Thread)
	public static class StateJARinJAR extends BenchmarkBase
	{

		public StateJARinJAR() 
		{
			super(jarInJar);
		}
		
		@Setup(Level.Trial)
		@Override
		public void setupFileLists() 
		{
			super.setupFileLists();
		}
		
		@Setup(Level.Trial)
		@Override
		public void setupFileSystem() 
		{
			super.setupFileSystem();
		}
		
		@TearDown(Level.Trial)
		@Override
		public void cleanupFileSystem() {
			super.cleanupFileSystem();
		}
	}
	
	
	
	
}
