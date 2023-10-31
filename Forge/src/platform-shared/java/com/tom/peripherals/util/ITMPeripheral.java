package com.tom.peripherals.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public interface ITMPeripheral {
	public class LuaInteruptedException extends LuaException {
		private static final long serialVersionUID = -7396879728472447101L;

	}
	public interface IComputer {
		void queueEvent(String event, Object[] arguments);
		String getAttachmentName();
		Object[] mapTo(Object[] in);
		Object[] mapFrom(Object[] in);
		Object[] pullEvent(String string) throws LuaException;
	}
	String getType();
	String[] getMethodNames();
	Object[] call(IComputer computer, String method, Object[] args) throws LuaException;
	default void attach(IComputer computer){}
	default void detach(IComputer computer){}
	public static class LuaException extends RuntimeException {
		private static final long serialVersionUID = -6487582951282118489L;
		private final int level;

		public LuaException() {
			this("error", 1);
		}

		public LuaException(String message ) {
			this(message, 1);
		}

		public LuaException(String message, int level ) {
			super(message);
			this.level = level;
		}
		public int getLevel() {
			return level;
		}
	}
	public interface ITMLuaObject {
		String[] getMethodNames();
		Object[] call(IComputer computer, String method, Object[] args) throws LuaException;
	}
	public static final Object[] E = new Object[0];
	public static class TMLuaObject implements ITMLuaObject {
		@SuppressWarnings("rawtypes")
		private static Map<Class, Pair<String[], Map<String, ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>>>> callCache = new HashMap<>();
		Pair<String[], Map<String, ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>>> call;
		public TMLuaObject() {
			call = callCache.computeIfAbsent(getClass(), c -> {
				Map<String, ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>> map = findLuaMethods(c);
				return Pair.of(map.keySet().stream().toArray(String[]::new), map);
			});

		}
		@Override
		public String[] getMethodNames() {
			return call.getKey();
		}

		@Override
		public Object[] call(IComputer computer, String method, Object[] args) throws LuaException {
			//System.out.println(method);
			ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException> c = call.getValue().get(method);
			if(c == null)throw new LuaException("No such method: " + method);
			else{
				try {
					return c.apply(this, computer, args);
				} catch (LuaException e){
					throw e;
				} catch (Throwable e) {
					e.printStackTrace();
					throw new LuaException("Java Exception: " + e.toString());
				}
			}
		}
		public Object[] callInt(IComputer computer, String method, Object[] args) throws LuaException, NoSuchMethodException {
			ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException> c = call.getValue().get(method);
			if(c == null)throw new NoSuchMethodException(method);
			else return c.apply(this, computer, args);
		}
	}

	public static class ReferenceableLuaObject extends TMLuaObject implements IReferenceable {
		private final String ref;

		public ReferenceableLuaObject(ReferenceManager mngr) {
			ref = mngr.createReference(this);
		}

		@LuaMethod
		@Override
		public Object ref() {
			return ref;
		}
	}

	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface LuaMethod {}

	@SuppressWarnings("rawtypes")
	public static Map<String, ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>> findLuaMethods(Class c){
		final MethodHandles.Lookup lookup = MethodHandles.lookup();
		Map<String, ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>> map =
				listMethods(lookup, c, LuaMethod.class, ThrowingTriFunction.class, Object[].class, IComputer.class, Object[].class);
		Map<String, ThrowingBiFunction<Object, Object[], Object[], LuaException>> map2 =
				listMethods(lookup, c, LuaMethod.class, ThrowingBiFunction.class, Object[].class, Object[].class);
		Map<String, ThrowingFunction<Object, Object[], LuaException>> map3 =
				listMethods(lookup, c, LuaMethod.class, ThrowingFunction.class, Object[].class);
		Map<String, ThrowingBiConsumer<Object, Object[], LuaException>> map4 =
				listMethods(lookup, c, LuaMethod.class, ThrowingBiConsumer.class, void.class, Object[].class);
		Map<String, ThrowingTriConsumer<Object, IComputer, Object[], LuaException>> map5 =
				listMethods(lookup, c, LuaMethod.class, ThrowingTriConsumer.class, void.class, IComputer.class, Object[].class);
		Map<String, ThrowingBiFunction<Object, Object[], Boolean, LuaException>> map6 =
				listMethods(lookup, c, LuaMethod.class, ThrowingBiFunction.class, boolean.class, Object[].class);
		Map<String, ThrowingBiFunction<Object, Object[], Integer, LuaException>> map7 =
				listMethods(lookup, c, LuaMethod.class, ThrowingBiFunction.class, int.class, Object[].class);
		Map<String, ThrowingBiFunction<Object, Object[], Object, LuaException>> map8 =
				listMethods(lookup, c, LuaMethod.class, ThrowingBiFunction.class, Object.class, Object[].class);
		Map<String, ThrowingFunction<Object, Integer, LuaException>> map9 =
				listMethods(lookup, c, LuaMethod.class, ThrowingFunction.class, int.class);
		Map<String, ThrowingFunction<Object, Boolean, LuaException>> map10 =
				listMethods(lookup, c, LuaMethod.class, ThrowingFunction.class, boolean.class);
		Map<String, ThrowingFunction<Object, Object, LuaException>> map11 =
				listMethods(lookup, c, LuaMethod.class, ThrowingFunction.class, Object.class);
		Map<String, ThrowingConsumer<Object, LuaException>> map12 =
				listMethods(lookup, c, LuaMethod.class, ThrowingConsumer.class, void.class);
		Map<String, ThrowingFunction<Object, Double, LuaException>> map13 =
				listMethods(lookup, c, LuaMethod.class, ThrowingFunction.class, double.class);
		MapStream.of(map2 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> l.apply(o, a)).put(map);
		MapStream.of(map3 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> l.apply(o)).put(map);
		MapStream.of(map4 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> {l.accept(o, a);return E;}).put(map);
		MapStream.of(map5 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> {l.accept(o, co, a);return E;}).put(map);
		MapStream.of(map6 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> new Object[]{l.apply(o, a)}).put(map);
		MapStream.of(map7 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> new Object[]{l.apply(o, a)}).put(map);
		MapStream.of(map8 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> new Object[]{l.apply(o, a)}).put(map);
		MapStream.of(map9 ).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> new Object[]{l.apply(o)}).put(map);
		MapStream.of(map10).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> new Object[]{l.apply(o)}).put(map);
		MapStream.of(map11).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> new Object[]{l.apply(o)}).put(map);
		MapStream.of(map12).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> {l.accept(o);return E;}).put(map);
		MapStream.of(map13).mapOValue(l -> (ThrowingTriFunction<Object, IComputer, Object[], Object[], LuaException>) (o, co, a) -> new Object[]{l.apply(o)}).put(map);
		map.put("listMethods", (o, co, a) -> map.keySet().stream().toArray(Object[]::new));
		return map;
	}

	public static class MapStream<T> {
		private Stream<Pair<String, T>> stream;

		public MapStream(Stream<Pair<String, T>> stream) {
			this.stream = stream;
		}

		public static <T> MapStream<T> of(Map<String, T> map) {
			return new MapStream<>(map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())));
		}

		public <R> MapStream<R> mapOValue(Function<T, R> f) {
			return new MapStream<>(stream.map(p -> Pair.of(p.getKey(), f.apply(p.getValue()))));
		}

		public void put(Map<String, T> map) {
			stream.forEach(e -> map.put(e.getKey(), e.getValue()));
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Map<String, T> listMethods(MethodHandles.Lookup lookup, Class clazz, Class annotation, Class<? super T> interfaceClass, Class returnType, Class... params){
		Map<String, T> map = new HashMap<>();
		try {
			Method im = interfaceClass.getDeclaredMethods()[0];
			MethodType imt = MethodType.methodType(im.getReturnType(), im.getParameterTypes());
			int args = im.getParameterCount();
			for(Method m : clazz.getMethods()){
				if(m.isAnnotationPresent(annotation) &&
						((m.getModifiers() & Modifier.STATIC) != 0 ? m.getParameterCount() : m.getParameterCount()+1) == args &&
						m.getReturnType() == returnType && Arrays.equals(m.getParameterTypes(), params)){
					try {
						MethodHandle mh = lookup.unreflect(m);
						T call = (T) LambdaMetafactory.metafactory(MethodHandles.lookup(), im.getName(),
								MethodType.methodType(interfaceClass), imt, mh, mh.type()).getTarget().invoke();
						map.put(m.getName(), call);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public static class ObjectWrapper implements ITMPeripheral {
		private List<IComputer> computers = new ArrayList<>();
		private final String type;
		private final TMLuaObject object;

		public ObjectWrapper(String type, TMLuaObject object) {
			this.type = type;
			this.object = object;
		}

		@Override
		public String getType() {
			return type;
		}

		@Override
		public String[] getMethodNames() {
			return object.getMethodNames();
		}

		@Override
		public Object[] call(IComputer computer, String method, Object[] args) throws LuaException {
			try {
				return object.callInt(computer, method, args);
			} catch (NoSuchMethodException e) {
				throw new LuaException("No such method");
			}
		}

		@Override
		public void attach(IComputer computer) {
			computers.add(computer);
		}

		@Override
		public void detach(IComputer computer) {
			computers.remove(computer);
		}

		public void queueEvent(String event, Object[] args) {
			Object[] a = new Object[args.length + 1];
			for (int i = 0;i < args.length;i++) {
				a[i + 1] = args[i];
			}
			for (IComputer c : computers) {
				a[0] = c.getAttachmentName();
				c.queueEvent(event, a);
			}
		}
	}
}
