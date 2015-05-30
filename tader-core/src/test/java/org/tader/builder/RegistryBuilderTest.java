package org.tader.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;
import org.tader.TestUtils;

@SuppressWarnings({ "rawtypes" })
public class RegistryBuilderTest {
	public static interface A {
		String doA();
	}

	public static interface B {
		String doA();

		String doB();
	}

	public static interface C {
		String doA();

		String doB();

		String doC();
	}

	public static interface Circular1 {
		String doCircular1();

		String doCircular2();
	}

	public static interface Circular2 {
		String doCircular1();

		String doCircular2();
	}

	public static class AImpl implements A {
		public AImpl() {
			incrementInstanceCount(A.class);
		}

		@Override
		public String doA() {
			return "AImpl.doA";
		}
	}

	public static class BImpl implements B {
		private final A a;

		public BImpl(A a) {
			incrementInstanceCount(B.class);
			this.a = a;
		}

		@Override
		public String doA() {
			return a.doA();
		}

		@Override
		public String doB() {
			return "BImpl.doB";
		}
	}

	public static class CImpl implements C {
		private final A a;
		private final B b;

		public CImpl(A a, B b) {
			incrementInstanceCount(C.class);
			this.a = a;
			this.b = b;
		}

		@Override
		public String doA() {
			return a.doA();
		}

		@Override
		public String doB() {
			return b.doB();
		}

		@Override
		public String doC() {
			return "CImpl.doC";
		}
	}

	public static class Circular1Impl implements Circular1 {
		private final Circular2 circular2;

		public Circular1Impl(Circular2 circular2) {
			incrementInstanceCount(Circular1.class);
			this.circular2 = circular2;
		}

		@Override
		public String doCircular1() {
			return "Circular1Impl.doCircular1";
		}

		@Override
		public String doCircular2() {
			return circular2.doCircular2();
		}
	}

	public static class Circular2Impl implements Circular2 {
		private final Circular1 circular1;

		public Circular2Impl(Circular1 circular1) {
			incrementInstanceCount(Circular2.class);
			this.circular1 = circular1;
		}

		@Override
		public String doCircular1() {
			return circular1.doCircular1();
		}

		@Override
		public String doCircular2() {
			return "Circular2Impl.doCircular2";
		}
	}

	private static final ConcurrentMap<Class, AtomicInteger> instanceCounts = new ConcurrentHashMap<Class, AtomicInteger>();

	static int incrementInstanceCount(Class type) {
		AtomicInteger count = instanceCounts.get(type);
		if (count == null) {
			AtomicInteger candidate = new AtomicInteger(0);
			AtomicInteger prev = instanceCounts.putIfAbsent(type, candidate);
			count = prev == null ? candidate : prev;
		}
		return count.incrementAndGet();
	}

	static int getInstanceCount(Class type) {
		AtomicInteger count = instanceCounts.get(type);
		return count == null ? 0 : count.get();
	}

	@Before
	public void before() {
		instanceCounts.clear();
	}

	@Test
	public void testDependencyInjection() {
		Registry registry = new RegistryBuilder().withServiceInstance(A.class, AImpl.class).withServiceInstance(B.class, BImpl.class)
				.withServiceInstance(C.class, CImpl.class).build();

		C c = registry.getService(C.class);

		assertEquals(0, getInstanceCount(A.class));
		assertEquals(0, getInstanceCount(B.class));
		assertEquals(0, getInstanceCount(C.class));

		assertEquals("CImpl.doC", c.doC());

		assertEquals(0, getInstanceCount(A.class));
		assertEquals(0, getInstanceCount(B.class));
		assertEquals(1, getInstanceCount(C.class));

		assertEquals("BImpl.doB", c.doB());

		assertEquals(0, getInstanceCount(A.class));
		assertEquals(1, getInstanceCount(B.class));
		assertEquals(1, getInstanceCount(C.class));

		assertEquals("AImpl.doA", c.doA());

		assertEquals(1, getInstanceCount(A.class));
		assertEquals(1, getInstanceCount(B.class));
		assertEquals(1, getInstanceCount(C.class));

		assertEquals("AImpl.doA", c.doA());
		assertEquals("BImpl.doB", c.doB());
		assertEquals("CImpl.doC", c.doC());

		assertEquals(1, getInstanceCount(A.class));
		assertEquals(1, getInstanceCount(B.class));
		assertEquals(1, getInstanceCount(C.class));
	}

	@Test
	public void testCircularDependencyInjection() {
		Registry registry = new RegistryBuilder().withServiceInstance(Circular1.class, Circular1Impl.class)
				.withServiceInstance(Circular2.class, Circular2Impl.class).build();

		Circular1 c1 = registry.getService(Circular1.class);
		Circular2 c2 = registry.getService(Circular2.class);

		assertEquals(0, getInstanceCount(Circular1.class));
		assertEquals(0, getInstanceCount(Circular2.class));

		assertEquals("Circular1Impl.doCircular1", c1.doCircular1());

		assertEquals(1, getInstanceCount(Circular1.class));
		assertEquals(0, getInstanceCount(Circular2.class));

		assertEquals("Circular2Impl.doCircular2", c1.doCircular2());

		assertEquals(1, getInstanceCount(Circular1.class));
		assertEquals(1, getInstanceCount(Circular2.class));

		assertEquals("Circular1Impl.doCircular1", c2.doCircular1());
		assertEquals("Circular2Impl.doCircular2", c2.doCircular2());

		assertEquals(1, getInstanceCount(Circular1.class));
		assertEquals(1, getInstanceCount(Circular2.class));

	}

	@Test
	public void testBuildOnce() {
		RegistryBuilder builder = new RegistryBuilder().withServiceInstance(A.class, AImpl.class).withProperty("p1", "v1");

		Registry registry = builder.build();
		assertNotNull(registry);
		try {
			builder.withService(List.class, new ArrayList());
			fail();
		} catch (RuntimeException e) {
		}
		try {
			builder.withServiceInstance(B.class, BImpl.class);
			fail();
		} catch (RuntimeException e) {
		}
		try {
			builder.withProperty("p2", "v2");
			fail();
		} catch (RuntimeException e) {
		}
		try {
			builder.build();
			fail();
		} catch (RuntimeException e) {
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetServiceInterfaces() {
		Registry registry = new RegistryBuilder().withServiceInstance(A.class, AImpl.class).withServiceInstance(B.class, BImpl.class)
				.build();

		assertEquals(TestUtils.newHashSet(A.class, B.class), registry.getServiceInterfaces());
	}

	public static interface Service {
		String getString();
	}

	public static class DelayService implements Service {
		final int instanceCount;

		public DelayService() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			instanceCount = incrementInstanceCount(DelayService.class);
		}

		@Override
		public String getString() {
			return String.valueOf(instanceCount);
		}
	}

	@Test
	public void testConstructorDelay() throws Exception {
		final Registry registry = new RegistryBuilder()
			.withServiceInstance(Service.class, DelayService.class)
			.build();

		Callable<String> callable = new Callable<String>() {
			@Override
			public String call() throws Exception {
				return registry.getService(Service.class).getString();
			}
		};
		ExecutorService pool = Executors.newFixedThreadPool(3);
		List<Future<String>> futures = new ArrayList<Future<String>>();
		futures.add(pool.submit(callable));
		futures.add(pool.submit(callable));
		futures.add(pool.submit(callable));

		List<String> values = new ArrayList<String>();
		for (Future<String> future : futures) {
			values.add(future.get());
		}

		assertEquals(Arrays.asList("1", "1", "1"), values);
		assertEquals(3, getInstanceCount(DelayService.class));
	}
}
