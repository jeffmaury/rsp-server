/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.jboss.tools.rsp.server.core.internal;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.jboss.tools.rsp.secure.crypto.CryptoException;
import org.jboss.tools.rsp.secure.model.ISecureStorageProvider;
import org.jboss.tools.rsp.server.ServerCoreActivator;
import org.jboss.tools.rsp.server.spi.model.IServerModel;

import com.google.gson.Gson;
/**
 * Helper class for storing runtime and server attributes.
 */
public abstract class SecuredBase extends Base {
	private ISecureStorageProvider secureStorage = null;
	
	/**
	 * Create a new object.
	 * 
	 * @param file
	 */
	public SecuredBase(File file, ISecureStorageProvider storage) {
		super(file);
		secureStorage = storage;
	}

	/**
	 * Create a new object.
	 * 
	 * @param file
	 * @param id
	 */
	public SecuredBase(File file, String id) {
		super(file, id);
	}
	
	/**
	 * Create a new object.
	 * 
	 * @param file
	 * @param id
	 * @param secure storage
	 */
	public SecuredBase(File file, String id, ISecureStorageProvider provider) {
		super(file, id); 
		secureStorage = provider;
	}

	/**
	 * Returns <code>true</code> if the attribute is currently set, and <code>false</code>
	 * otherwise.
	 * 
	 * @param attributeName
	 * @return <code>true</code> if the attribute is currently set, and <code>false</code>
	 *    otherwise
	 */
	public boolean isAttributeSet(String attributeName) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX))
			return super.isAttributeSet(attributeName);
		if( secureStorage.getSecureStorage() != null) {
			String securedNode = getSecuredKey();
			try {
				return secureStorage.getSecureStorage().propertyExists(securedNode, attributeName);
			} catch(CryptoException ce) {
				// TODO log
			}
		}
		return false;
	}

	public String getAttribute(String attributeName, String defaultValue) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX))
			return super.getAttribute(attributeName, defaultValue);
		if( secureStorage != null && secureStorage.getSecureStorage() != null ) {
			String securedNode = getSecuredKey();
			try {
				return secureStorage.getSecureStorage().getNode(securedNode)
					.getStringProperty(attributeName, defaultValue);
			} catch(CryptoException ce) {
				// TODO log
			}
		}
		return defaultValue;
	}

	public int getAttribute(String attributeName, int defaultValue) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX))
			return super.getAttribute(attributeName, defaultValue);
		if( secureStorage != null && secureStorage.getSecureStorage() != null ) {
			String securedNode = getSecuredKey();
			try {
				return secureStorage.getSecureStorage().getNode(securedNode)
					.getIntegerProperty(attributeName, defaultValue);
			} catch(CryptoException ce) {
				// TODO log
			}
		}
		return defaultValue;
	}

	public boolean getAttribute(String attributeName, boolean defaultValue) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX))
			return super.getAttribute(attributeName, defaultValue);
		if( secureStorage != null && secureStorage.getSecureStorage() != null ) {
			String securedNode = getSecuredKey();
			try {
				return secureStorage.getSecureStorage().getNode(securedNode)
					.getBooleanProperty(attributeName, defaultValue);
			} catch(CryptoException ce) {
				// TODO log
			}
		}
		return defaultValue;
	}

	@SuppressWarnings("unchecked")
	public List<String> getAttribute(String attributeName, List<String> defaultValue) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX))
			return super.getAttribute(attributeName, defaultValue);
		if( secureStorage != null && secureStorage.getSecureStorage() != null ) {
			String securedNode = getSecuredKey();
			try {
				String fromSecure = secureStorage.getSecureStorage().getNode(securedNode)
					.getStringProperty(attributeName, (String)null);
				if( fromSecure != null ) {
					List<String> ret = new Gson().fromJson(fromSecure, List.class);
					return ret;
				}
			} catch(CryptoException ce) {
				// TODO log
			}
		}
		return defaultValue;
	}

	public Map getAttribute(String attributeName, Map defaultValue) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX))
			return super.getAttribute(attributeName, defaultValue);
		if( secureStorage != null && secureStorage.getSecureStorage() != null ) {
			String securedNode = getSecuredKey();
			try {
				String fromSecure = secureStorage.getSecureStorage().getNode(securedNode)
					.getStringProperty(attributeName, (String)null);
				if( fromSecure != null ) {
					Map<String,String> ret = new Gson().fromJson(fromSecure, Map.class);
					return ret;
				}
			} catch(CryptoException ce) {
				// TODO log
			}
		}
		return defaultValue;
	}

	

	public void setAttribute(String attributeName, int value) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX)) {
			super.setAttribute(attributeName, value);
			return;
		}
		if( secureStorage != null && secureStorage.getSecureStorage() != null ) {
			String securedNode = getSecuredKey();
			try {
				secureStorage.getSecureStorage().getNode(securedNode)
					.setIntegerProperty(attributeName, value);
			} catch(CryptoException ce) {
				// TODO log
			}
		}
	}

	public void setAttribute(String attributeName, boolean value) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX)) {
			super.setAttribute(attributeName, value);
			return;
		}
		if( secureStorage != null && secureStorage.getSecureStorage() != null ) {
			String securedNode = getSecuredKey();
			try {
				secureStorage.getSecureStorage().getNode(securedNode)
					.setBooleanProperty(attributeName, value);
			} catch(CryptoException ce) {
				// TODO log
			}
		}
	}

	public void setAttribute(String attributeName, String value) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX)) {
			super.setAttribute(attributeName, value);
			return;
		}
		
		if( secureStorage != null && secureStorage.getSecureStorage() != null ) {
			String securedNode = getSecuredKey();
			try {
				secureStorage.getSecureStorage().getNode(securedNode)
					.setStringProperty(attributeName, value);
			} catch(CryptoException ce) {
				// TODO log
			}
		}
	}

	public void setAttribute(String attributeName, List<String> value) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX)) {
			super.setAttribute(attributeName, value);
			return;
		}
		
		if( secureStorage != null && secureStorage.getSecureStorage() != null ) {
			String securedNode = getSecuredKey();
			Gson gson = new Gson();
			String val = gson.toJson(value);
			try {
				secureStorage.getSecureStorage().getNode(securedNode)
					.setStringProperty(attributeName, val);
			} catch(CryptoException ce) {
				// TODO log
			}
		}
	}

	public void setAttribute(String attributeName, Map value) {
		if( !attributeName.startsWith(IServerModel.SECURE_ATTRIBUTE_PREFIX)) {
			super.setAttribute(attributeName, value);
			return;
		}
		
		if( secureStorage != null && secureStorage.getSecureStorage() != null ) {
			String securedNode = getSecuredKey();
			Gson gson = new Gson();
			String val = gson.toJson(value);
			try {
				secureStorage.getSecureStorage().getNode(securedNode)
					.setStringProperty(attributeName, val);
			} catch(CryptoException ce) {
				// TODO log
			}
		}
	}
	
	
	private String getSecuredKey() {
		return ServerCoreActivator.BUNDLE_ID + "/servers/" + getId() + "/"; 
	}
	
}