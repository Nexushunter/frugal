#
# Autogenerated by Frugal Compiler (1.24.1)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#



import inspect
import sys
import traceback

from thrift.Thrift import TApplicationException
from thrift.Thrift import TMessageType
from thrift.Thrift import TType
from frugal.middleware import Method
from frugal.subscription import FSubscription

from .ttypes import *




class FooPublisher(object):
    """
    And this is a scope docstring.
    """

    _DELIMITER = '.'

    def __init__(self, provider, middleware=None):
        """
        Create a new FooPublisher.

        Args:
            provider: FScopeProvider
            middleware: ServiceMiddleware or list of ServiceMiddleware
        """

        if middleware and not isinstance(middleware, list):
            middleware = [middleware]
        self._transport, protocol_factory = provider.new()
        self._protocol = protocol_factory.get_protocol(self._transport)
        self._methods = {
            'publish_Foo': Method(self._publish_Foo, middleware),
            'publish_Bar': Method(self._publish_Bar, middleware),
        }

    async def open(self):
        await self._transport.open()

    async def close(self):
        await self._transport.close()

    async def publish_Foo(self, ctx, baz, req):
        """
        This is an operation docstring.
        
        Args:
            ctx: FContext
            baz: string
            req: Thing
        """
        await self._methods['publish_Foo']([ctx, baz, req])

    async def _publish_Foo(self, ctx, baz, req):
        op = 'Foo'
        prefix = 'foo.bar.{}.qux.'.format(baz)
        topic = '{}Foo{}{}'.format(prefix, self._DELIMITER, op)
        oprot = self._protocol
        await self._transport.lock_topic(topic)
        try:
            oprot.write_request_headers(ctx)
            oprot.writeMessageBegin(op, TMessageType.CALL, 0)
            req.write(oprot)
            oprot.writeMessageEnd()
            await oprot.get_transport().flush()
        finally:
            self._transport.unlock_topic()


    async def publish_Bar(self, ctx, baz, req):
        """
        Args:
            ctx: FContext
            baz: string
            req: Stuff
        """
        await self._methods['publish_Bar']([ctx, baz, req])

    async def _publish_Bar(self, ctx, baz, req):
        op = 'Bar'
        prefix = 'foo.bar.{}.qux.'.format(baz)
        topic = '{}Foo{}{}'.format(prefix, self._DELIMITER, op)
        oprot = self._protocol
        await self._transport.lock_topic(topic)
        try:
            oprot.write_request_headers(ctx)
            oprot.writeMessageBegin(op, TMessageType.CALL, 0)
            req.write(oprot)
            oprot.writeMessageEnd()
            await oprot.get_transport().flush()
        finally:
            self._transport.unlock_topic()

