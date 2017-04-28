/**
 * Autogenerated by Frugal Compiler (2.2.3)
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *
 * @generated
 */

package variety.java;

import com.workiva.frugal.FContext;
import com.workiva.frugal.exception.TApplicationExceptionType;
import com.workiva.frugal.middleware.InvocationHandler;
import com.workiva.frugal.middleware.ServiceMiddleware;
import com.workiva.frugal.protocol.*;
import com.workiva.frugal.provider.FScopeProvider;
import com.workiva.frugal.transport.FPublisherTransport;
import com.workiva.frugal.transport.FSubscriberTransport;
import com.workiva.frugal.transport.FSubscription;
import com.workiva.frugal.transport.TMemoryOutputBuffer;
import org.apache.thrift.TException;
import org.apache.thrift.TApplicationException;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.protocol.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.Generated;




@Generated(value = "Autogenerated by Frugal Compiler (2.2.3)", date = "2015-11-24")
public class EventsSubscriber {

	/**
	 * This docstring gets added to the generated code because it has
	 * the @ sign. Prefix specifies topic prefix tokens, which can be static or
	 * variable.
	 */
	public interface Iface {
		/**
		 * This is a docstring.
		 */
		public FSubscription subscribeEventCreated(String user, final EventCreatedHandler handler) throws TException;

		public FSubscription subscribeSomeInt(String user, final SomeIntHandler handler) throws TException;

		public FSubscription subscribeSomeStr(String user, final SomeStrHandler handler) throws TException;

		public FSubscription subscribeSomeList(String user, final SomeListHandler handler) throws TException;

	}

	public interface EventCreatedHandler {
		void onEventCreated(FContext ctx, Event req);
	}

	public interface SomeIntHandler {
		void onSomeInt(FContext ctx, long req);
	}

	public interface SomeStrHandler {
		void onSomeStr(FContext ctx, String req);
	}

	public interface SomeListHandler {
		void onSomeList(FContext ctx, java.util.List<java.util.Map<Long, Event>> req);
	}

	/**
	 * This docstring gets added to the generated code because it has
	 * the @ sign. Prefix specifies topic prefix tokens, which can be static or
	 * variable.
	 */
	public static class Client implements Iface {
		private static final String DELIMITER = ".";
		private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

		private final FScopeProvider provider;
		private final ServiceMiddleware[] middleware;

		public Client(FScopeProvider provider, ServiceMiddleware... middleware) {
			this.provider = provider;
			List<ServiceMiddleware> combined = Arrays.asList(middleware);
			combined.addAll(provider.getMiddleware());
			this.middleware = combined.toArray(new ServiceMiddleware[0]);
		}

		/**
		 * This is a docstring.
		 */
		public FSubscription subscribeEventCreated(String user, final EventCreatedHandler handler) throws TException {
			final String op = "EventCreated";
			String prefix = String.format("foo.%s.", user);
			final String topic = String.format("%sEvents%s%s", prefix, DELIMITER, op);
			final FScopeProvider.Subscriber subscriber = provider.buildSubscriber();
			final FSubscriberTransport transport = subscriber.getTransport();
			final EventCreatedHandler proxiedHandler = InvocationHandler.composeMiddleware(handler, EventCreatedHandler.class, middleware);
			transport.subscribe(topic, recvEventCreated(op, subscriber.getProtocolFactory(), proxiedHandler));
			return FSubscription.of(topic, transport);
		}

		private FAsyncCallback recvEventCreated(String op, FProtocolFactory pf, EventCreatedHandler handler) {
			return new FAsyncCallback() {
				public void onMessage(TTransport tr) throws TException {
					FProtocol iprot = pf.getProtocol(tr);
					FContext ctx = iprot.readRequestHeader();
					TMessage msg = iprot.readMessageBegin();
					if (!msg.name.equals(op)) {
						TProtocolUtil.skip(iprot, TType.STRUCT);
						iprot.readMessageEnd();
						throw new TApplicationException(TApplicationExceptionType.UNKNOWN_METHOD);
					}
					Event received = new Event();
					received.read(iprot);
					iprot.readMessageEnd();
					handler.onEventCreated(ctx, received);
				}
			};
		}



		public FSubscription subscribeSomeInt(String user, final SomeIntHandler handler) throws TException {
			final String op = "SomeInt";
			String prefix = String.format("foo.%s.", user);
			final String topic = String.format("%sEvents%s%s", prefix, DELIMITER, op);
			final FScopeProvider.Subscriber subscriber = provider.buildSubscriber();
			final FSubscriberTransport transport = subscriber.getTransport();
			final SomeIntHandler proxiedHandler = InvocationHandler.composeMiddleware(handler, SomeIntHandler.class, middleware);
			transport.subscribe(topic, recvSomeInt(op, subscriber.getProtocolFactory(), proxiedHandler));
			return FSubscription.of(topic, transport);
		}

		private FAsyncCallback recvSomeInt(String op, FProtocolFactory pf, SomeIntHandler handler) {
			return new FAsyncCallback() {
				public void onMessage(TTransport tr) throws TException {
					FProtocol iprot = pf.getProtocol(tr);
					FContext ctx = iprot.readRequestHeader();
					TMessage msg = iprot.readMessageBegin();
					if (!msg.name.equals(op)) {
						TProtocolUtil.skip(iprot, TType.STRUCT);
						iprot.readMessageEnd();
						throw new TApplicationException(TApplicationExceptionType.UNKNOWN_METHOD);
					}
					long received = iprot.readI64();
					iprot.readMessageEnd();
					handler.onSomeInt(ctx, received);
				}
			};
		}



		public FSubscription subscribeSomeStr(String user, final SomeStrHandler handler) throws TException {
			final String op = "SomeStr";
			String prefix = String.format("foo.%s.", user);
			final String topic = String.format("%sEvents%s%s", prefix, DELIMITER, op);
			final FScopeProvider.Subscriber subscriber = provider.buildSubscriber();
			final FSubscriberTransport transport = subscriber.getTransport();
			final SomeStrHandler proxiedHandler = InvocationHandler.composeMiddleware(handler, SomeStrHandler.class, middleware);
			transport.subscribe(topic, recvSomeStr(op, subscriber.getProtocolFactory(), proxiedHandler));
			return FSubscription.of(topic, transport);
		}

		private FAsyncCallback recvSomeStr(String op, FProtocolFactory pf, SomeStrHandler handler) {
			return new FAsyncCallback() {
				public void onMessage(TTransport tr) throws TException {
					FProtocol iprot = pf.getProtocol(tr);
					FContext ctx = iprot.readRequestHeader();
					TMessage msg = iprot.readMessageBegin();
					if (!msg.name.equals(op)) {
						TProtocolUtil.skip(iprot, TType.STRUCT);
						iprot.readMessageEnd();
						throw new TApplicationException(TApplicationExceptionType.UNKNOWN_METHOD);
					}
					String received = iprot.readString();
					iprot.readMessageEnd();
					handler.onSomeStr(ctx, received);
				}
			};
		}



		public FSubscription subscribeSomeList(String user, final SomeListHandler handler) throws TException {
			final String op = "SomeList";
			String prefix = String.format("foo.%s.", user);
			final String topic = String.format("%sEvents%s%s", prefix, DELIMITER, op);
			final FScopeProvider.Subscriber subscriber = provider.buildSubscriber();
			final FSubscriberTransport transport = subscriber.getTransport();
			final SomeListHandler proxiedHandler = InvocationHandler.composeMiddleware(handler, SomeListHandler.class, middleware);
			transport.subscribe(topic, recvSomeList(op, subscriber.getProtocolFactory(), proxiedHandler));
			return FSubscription.of(topic, transport);
		}

		private FAsyncCallback recvSomeList(String op, FProtocolFactory pf, SomeListHandler handler) {
			return new FAsyncCallback() {
				public void onMessage(TTransport tr) throws TException {
					FProtocol iprot = pf.getProtocol(tr);
					FContext ctx = iprot.readRequestHeader();
					TMessage msg = iprot.readMessageBegin();
					if (!msg.name.equals(op)) {
						TProtocolUtil.skip(iprot, TType.STRUCT);
						iprot.readMessageEnd();
						throw new TApplicationException(TApplicationExceptionType.UNKNOWN_METHOD);
					}
					org.apache.thrift.protocol.TList elem277 = iprot.readListBegin();
					java.util.List<java.util.Map<Long, Event>> received = new ArrayList<java.util.Map<Long, Event>>(elem277.size);
					for (int elem278 = 0; elem278 < elem277.size; ++elem278) {
						org.apache.thrift.protocol.TMap elem280 = iprot.readMapBegin();
						java.util.Map<Long, Event> elem279 = new HashMap<Long,Event>(2*elem280.size);
						for (int elem281 = 0; elem281 < elem280.size; ++elem281) {
							long elem283 = iprot.readI64();
							Event elem282 = new Event();
							elem282.read(iprot);
							elem279.put(elem283, elem282);
						}
						iprot.readMapEnd();
						received.add(elem279);
					}
					iprot.readListEnd();
					iprot.readMessageEnd();
					handler.onSomeList(ctx, received);
				}
			};
		}

	}

}
