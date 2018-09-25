/*******************************************************************************
 * Copyright (c) 2018 Red Hat, Inc. Distributed under license by Red Hat, Inc.
 * All rights reserved. This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Red Hat, Inc.
 ******************************************************************************/
package org.jboss.tools.rsp.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.jsonrpc.MessageConsumer;
import org.eclipse.lsp4j.jsonrpc.RemoteEndpoint;
import org.eclipse.lsp4j.jsonrpc.json.ConcurrentMessageProcessor;
import org.eclipse.lsp4j.jsonrpc.json.MessageJsonHandler;
import org.eclipse.lsp4j.jsonrpc.json.StreamMessageProducer;
import org.eclipse.lsp4j.jsonrpc.services.ServiceEndpoints;

public class SocketLauncher<T> implements Launcher<T> {

	private final Launcher<T> launcher;
	private Future<Void> startListeningResult;
	private Socket socket;

	public SocketLauncher(Object localService, Class<T> remoteInterface, Socket socket) throws IOException {
		this.launcher = Launcher.createLauncher(localService, remoteInterface, socket.getInputStream(),
				socket.getOutputStream());
		this.socket = socket;
	}

	public SocketLauncher(Object localService, Class<T> remoteInterface, Socket socket, PrintWriter tracing)
			throws IOException {
		Launcher<T> launcherTmp = createBuilder(remoteInterface).setLocalService(localService).setRemoteInterface(remoteInterface)
				.setInput(socket.getInputStream()).setOutput(socket.getOutputStream()).traceMessages(tracing).create();
		this.launcher = launcherTmp;
		this.socket = socket;
	}

	protected Builder<T> createBuilder(Class<T> remoteInterface) {
		return new Builder<T>();
	}
	
	
	public CompletableFuture<Void> startListening() {
		return CompletableFuture.runAsync(() -> {
			try {
				this.startListeningResult = this.launcher.startListening();
				startListeningResult.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}, Executors.newSingleThreadExecutor());
	}

	public T getRemoteProxy() {
		return this.launcher.getRemoteProxy();
	}


	@Override
	public RemoteEndpoint getRemoteEndpoint() {
		return this.launcher.getRemoteEndpoint();
	}

	public void close() {
		if( startListeningResult != null ) {
			startListeningResult.cancel(true);
		}
		try {
			socket.close();
		} catch(IOException ioe) {
		}
	}
	
	public Future<Void> getStartListeningResult() {
		return startListeningResult;
	}
}
