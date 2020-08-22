package mcenderdragon.nio.jarInjar.jmh;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import mcenderdragon.nio.jarInjar.HelperURIFormatter;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class Benchmarks
{
	
	public static final URI[] jar;
	public static final URI[] jarInJar;
	static
	{
		String[] filePath = new String[] {"./src/main/resources/small.jar", "./src/main/resources/medium.jar", "./src/main/resources/large.jar"};
		jar = new URI[filePath.length];
		jarInJar = new URI[filePath.length];
		int i=0;
		for(String s : filePath)
		{
			File base = new File(s).getAbsoluteFile();
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
			jar[i] = j;
			jarInJar[i] = jij;
			i++;
		}
		
	}
	
	//open jar with jar in jar
	//open jar in jar (with jar in jar)
	//open jar in jar (with only jar)
	@State(Scope.Thread)
	public static class StateBase extends BenchmarkBase
	{
		public StateBase(URI jarFile) 
		{
			super(jarFile);
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
	
	@State(Scope.Thread)
	public static class StateJARSmall extends StateBase
	{
		public StateJARSmall() 
		{
			super(jar[0]);
		}
	}
	
	@State(Scope.Thread)
	public static class StateJARinJARSmall extends StateBase
	{

		public StateJARinJARSmall() 
		{
			super(jarInJar[0]);
		}
	}
	
	@State(Scope.Thread)
	public static class StateJARMedium extends StateBase
	{
		public StateJARMedium() 
		{
			super(jar[1]);
		}
	}
	
	@State(Scope.Thread)
	public static class StateJARinJARMedium extends StateBase
	{

		public StateJARinJARMedium() 
		{
			super(jarInJar[1]);
		}
	}
	
	@State(Scope.Thread)
	public static class StateJARLarge extends StateBase
	{
		public StateJARLarge() 
		{
			super(jar[2]);
		}
	}
	
	@State(Scope.Thread)
	public static class StateJARinJARLarge extends StateBase
	{

		public StateJARinJARLarge() 
		{
			super(jarInJar[2]);
		}
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
    @Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJARinJAR_small_ordered(StateJARinJARSmall state, Blackhole hole)
	{
		state.readSequential(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
	@Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJARinJAR_small_unordered(StateJARinJARSmall state, Blackhole hole)
	{
		state.readRandom(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
	@Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJAR_small_ordered(StateJARSmall state, Blackhole hole)
	{
		state.readSequential(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
	@Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJAR_small_unordered(StateJARSmall state, Blackhole hole)
	{
		state.readRandom(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
    @Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJARinJAR_medium_ordered(StateJARinJARMedium state, Blackhole hole)
	{
		state.readSequential(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
	@Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJARinJAR_medium_unordered(StateJARinJARMedium state, Blackhole hole)
	{
		state.readRandom(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
	@Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJAR_medium_ordered(StateJARMedium state, Blackhole hole)
	{
		state.readSequential(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
	@Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJAR_medium_unordered(StateJARMedium state, Blackhole hole)
	{
		state.readRandom(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
    @Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJARinJAR_large_ordered(StateJARinJARLarge state, Blackhole hole)
	{
		state.readSequential(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
	@Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJARinJAR_large_unordered(StateJARinJARLarge state, Blackhole hole)
	{
		state.readRandom(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
	@Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJAR_large_ordered(StateJARLarge state, Blackhole hole)
	{
		state.readSequential(hole);
	}
	
	@Benchmark
	@Fork(value = 3, warmups = 1)
	@Measurement(iterations = 5)
    @Warmup(iterations = 3)
	public static void benchJAR_large_unordered(StateJARLarge state, Blackhole hole)
	{
		state.readRandom(hole);
	}
	
	
	
	
}
