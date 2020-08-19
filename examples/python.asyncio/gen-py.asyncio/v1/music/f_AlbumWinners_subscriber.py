#
# Autogenerated by Frugal Compiler (3.9.9)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#



import inspect
import sys
import traceback

from thrift.Thrift import TApplicationException
from thrift.Thrift import TMessageType
from thrift.Thrift import TType
from frugal.exceptions import TApplicationExceptionType
from frugal.middleware import Method
from frugal.subscription import FSubscription
from frugal.transport import TMemoryOutputBuffer

from .ttypes import *




class AlbumWinnersSubscriber(object):
    """
    Scopes are a Frugal extension to the IDL for declaring PubSub
    semantics. Subscribers to this scope will be notified if they win a contest.
    Scopes must have a prefix.
    """

    _DELIMITER = '.'

    def __init__(self, provider, middleware=None):
        """
        Create a new AlbumWinnersSubscriber.

        Args:
            provider: FScopeProvider
            middleware: ServiceMiddleware or list of ServiceMiddleware
        """

        middleware = middleware or []
        if middleware and not isinstance(middleware, list):
            middleware = [middleware]
        middleware += provider.get_middleware()
        self._middleware = middleware
        self._provider = provider

    async def subscribe_ContestStart(self, ContestStart_handler):
        """
            ContestStart_handler: function which takes FContext and list<Album>
        """

        op = 'ContestStart'
        prefix = 'v1.music.'
        topic = '{}AlbumWinners{}{}'.format(prefix, self._DELIMITER, op)

        transport, protocol_factory = self._provider.new_subscriber()
        await transport.subscribe(topic, self._recv_ContestStart(protocol_factory, op, ContestStart_handler))
        return FSubscription(topic, transport)

    def _recv_ContestStart(self, protocol_factory, op, handler):
        method = Method(handler, self._middleware)

        async def callback(transport):
            iprot = protocol_factory.get_protocol(transport)
            ctx = iprot.read_request_headers()
            mname, _, _ = iprot.readMessageBegin()
            if mname != op:
                iprot.skip(TType.STRUCT)
                iprot.readMessageEnd()
                raise TApplicationException(TApplicationExceptionType.UNKNOWN_METHOD)
            req = []
            (_, elem4) = iprot.readListBegin()
            for _ in range(elem4):
                elem5 = Album()
                elem5.read(iprot)
                req.append(elem5)
            iprot.readListEnd()
            iprot.readMessageEnd()
            try:
                ret = method([ctx, req])
                if inspect.iscoroutine(ret):
                    await ret
            except:
                traceback.print_exc()
                sys.exit(1)

        return callback



    async def subscribe_TimeLeft(self, TimeLeft_handler):
        """
            TimeLeft_handler: function which takes FContext and Minutes
        """

        op = 'TimeLeft'
        prefix = 'v1.music.'
        topic = '{}AlbumWinners{}{}'.format(prefix, self._DELIMITER, op)

        transport, protocol_factory = self._provider.new_subscriber()
        await transport.subscribe(topic, self._recv_TimeLeft(protocol_factory, op, TimeLeft_handler))
        return FSubscription(topic, transport)

    def _recv_TimeLeft(self, protocol_factory, op, handler):
        method = Method(handler, self._middleware)

        async def callback(transport):
            iprot = protocol_factory.get_protocol(transport)
            ctx = iprot.read_request_headers()
            mname, _, _ = iprot.readMessageBegin()
            if mname != op:
                iprot.skip(TType.STRUCT)
                iprot.readMessageEnd()
                raise TApplicationException(TApplicationExceptionType.UNKNOWN_METHOD)
            req = iprot.readDouble()
            iprot.readMessageEnd()
            try:
                ret = method([ctx, req])
                if inspect.iscoroutine(ret):
                    await ret
            except:
                traceback.print_exc()
                sys.exit(1)

        return callback



    async def subscribe_Winner(self, Winner_handler):
        """
            Winner_handler: function which takes FContext and Album
        """

        op = 'Winner'
        prefix = 'v1.music.'
        topic = '{}AlbumWinners{}{}'.format(prefix, self._DELIMITER, op)

        transport, protocol_factory = self._provider.new_subscriber()
        await transport.subscribe(topic, self._recv_Winner(protocol_factory, op, Winner_handler))
        return FSubscription(topic, transport)

    def _recv_Winner(self, protocol_factory, op, handler):
        method = Method(handler, self._middleware)

        async def callback(transport):
            iprot = protocol_factory.get_protocol(transport)
            ctx = iprot.read_request_headers()
            mname, _, _ = iprot.readMessageBegin()
            if mname != op:
                iprot.skip(TType.STRUCT)
                iprot.readMessageEnd()
                raise TApplicationException(TApplicationExceptionType.UNKNOWN_METHOD)
            req = Album()
            req.read(iprot)
            iprot.readMessageEnd()
            try:
                ret = method([ctx, req])
                if inspect.iscoroutine(ret):
                    await ret
            except:
                traceback.print_exc()
                sys.exit(1)

        return callback




