/*******************************************************************************
 * Copyright (c) 2013 hangum.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     hangum - initial API and implementation
 ******************************************************************************/
package com.hangum.tadpole.preference.ui;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.hangum.tadpole.commons.google.analytics.AnalyticCaller;
import com.hangum.tadpole.commons.libs.core.define.PublicTadpoleDefine;
import com.hangum.tadpole.commons.libs.core.message.CommonMessages;
import com.hangum.tadpole.commons.otp.core.GetOTPCode;
import com.hangum.tadpole.engine.manager.TadpoleApplicationContextManager;
import com.hangum.tadpole.engine.query.dao.system.UserDAO;
import com.hangum.tadpole.engine.query.sql.TadpoleSystem_UserQuery;
import com.hangum.tadpole.engine.query.sql.TadpoleSystem_UserRole;
import com.hangum.tadpole.engine.utils.TimeZoneUtil;
import com.hangum.tadpole.preference.Messages;
import com.hangum.tadpole.preference.define.AdminPreferenceDefine;
import com.hangum.tadpole.preference.define.GetAdminPreference;
import com.hangum.tadpole.preference.dialogs.user.ChangePasswordDialog;
import com.hangum.tadpole.preference.dialogs.user.ChangeUsePersonalToGrouprDialog;
import com.hangum.tadpole.session.manager.SessionManager;

/**
 * 사용자 정보 수정
 * 
 * @author hangum
 *
 */
public class UserInfoPerference extends TadpoleDefaulPreferencePage implements IWorkbenchPreferencePage {
	private static final Logger logger = Logger.getLogger(UserInfoPerference.class);
	
	private Text textEmail;
	private Text textName;
	
	private Combo comboLanguage;
	private Combo comboTimezone;
	
	/** OTP code */
	private String secretKey = ""; //$NON-NLS-1$
	private Button btnGetOptCode;
	private Text textSecretKey;
	private Text textQRCodeURL;
	private Text textOTPCode;
	private Composite container_1;

	/**
	 * Create the preference page.
	 */
	public UserInfoPerference() {
	}

	/**
	 * Create contents of the preference page.
	 * @param parent
	 */
	@Override
	public Control createContents(Composite parent) {
		container_1 = new Composite(parent, SWT.NULL);
		container_1.setLayout(new GridLayout(2, false));

		if(StringUtils.equals(GetAdminPreference.getLoginMethod(), AdminPreferenceDefine.SYSTEM_LOGIN_METHOD_LDAP)) {
			ldapView(container_1);
		} else {
			if(TadpoleApplicationContextManager.isPersonOperationType()) {
				personView(container_1);
			} else {
				groupView(container_1);
			}
		}
		
		// google analytic
		AnalyticCaller.track(this.getClass().getName());
		
		return container_1;
	}
	
	/**
	 * 그룹 사용
	 * 
	 * @param container
	 */
	private void ldapView(Composite container) {
		Label lblName = new Label(container, SWT.NONE);
		lblName.setText(Messages.get().UserInfoPerference_5);
		
		textName = new Text(container, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.setText(SessionManager.getName());
		textName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				isValid();
			}
		});
		
		Label lblEmail = new Label(container, SWT.NONE);
		lblEmail.setText(Messages.get().UserInfoPerference_2);
		
		textEmail = new Text(container, SWT.BORDER);
		textEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textEmail.setText(SessionManager.getEMAIL());
		textEmail.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				isValid();
			}
		});
	}
	
	/**
	 * 개인 사용자 뷰
	 * 
	 * @param container
	 */
	private void personView(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText(Messages.get().UserInfoPerference_7);
		
		Button btnNewButton = new Button(container, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChangeUsePersonalToGrouprDialog dialog = new ChangeUsePersonalToGrouprDialog(getShell());
				dialog.open();
				
			}
		});
		btnNewButton.setText(Messages.get().UserInfoPerference_10);
	}
	
	/**
	 * 그룹 사용
	 * 
	 * @param container
	 */
	private void groupView(Composite container) {
		Label lblName = new Label(container, SWT.NONE);
		lblName.setText(Messages.get().UserInfoPerference_5);
		
		textName = new Text(container, SWT.BORDER);
		textName.setEditable(false);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textName.setText(SessionManager.getName());
		
		Label lblEmail = new Label(container, SWT.NONE);
		lblEmail.setText(Messages.get().UserInfoPerference_2);
		
		textEmail = new Text(container, SWT.BORDER);
		textEmail.setEditable(false);
		textEmail.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textEmail.setText(SessionManager.getEMAIL());
		
		Label lblLanguage = new Label(container_1, SWT.NONE);
		lblLanguage.setText(Messages.get().Language);
		comboLanguage = new Combo(container, SWT.READ_ONLY);
		comboLanguage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboLanguage.add(Locale.ENGLISH.getDisplayLanguage(Locale.ENGLISH));
		comboLanguage.add(Locale.KOREAN.getDisplayLanguage(Locale.KOREAN));
		comboLanguage.setData(Locale.ENGLISH.getDisplayLanguage(Locale.ENGLISH), Locale.ENGLISH);
		comboLanguage.setData(Locale.KOREAN.getDisplayLanguage(Locale.KOREAN), Locale.KOREAN);
		
		Label lblTimezone = new Label(container_1, SWT.NONE);
		lblTimezone.setText(Messages.get().TimeZone);
		comboTimezone = new Combo(container, SWT.READ_ONLY);
		comboTimezone.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for (String timzon : TimeZoneUtil.getTimezoneList()) {
			comboTimezone.add(timzon);
		}
		comboTimezone.setText(SessionManager.getTimezone());
		
		new Label(container, SWT.NONE);
		
		Button btnPasswordChange = new Button(container_1, SWT.NONE);
		btnPasswordChange.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ChangePasswordDialog dialog = new ChangePasswordDialog(getShell());
				dialog.open();
			}
		});
		btnPasswordChange.setText(Messages.get().PasswordChange);
		
		Label lblPasswordDescription = new Label(container, SWT.NONE);
		lblPasswordDescription.setText(Messages.get().UserInfoPerference_11);
		lblPasswordDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		// google auth
		Group grpGoogleAuth = new Group(container, SWT.NONE);
		grpGoogleAuth.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpGoogleAuth.setText(Messages.get().UserInfoPerference_grpGoogleAuth_text);
		grpGoogleAuth.setLayout(new GridLayout(2, false));
		
		btnGetOptCode = new Button(grpGoogleAuth, SWT.CHECK);
		btnGetOptCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				generateGoogleOTP();
			}
		});
		if(PublicTadpoleDefine.YES_NO.YES.name().equals(SessionManager.getUseOTP())) {
			btnGetOptCode.setSelection(true);
		}
		btnGetOptCode.setText(Messages.get().UserInfoPerference_btnGoogleOtp_text_1);
		new Label(grpGoogleAuth, SWT.NONE);
		
		Label lblSecretKey = new Label(grpGoogleAuth, SWT.NONE);
		lblSecretKey.setText(Messages.get().UserInfoPerference_lblSecretKey_text_1);
		
		textSecretKey = new Text(grpGoogleAuth, SWT.BORDER);
		textSecretKey.setText(SessionManager.getOTPSecretKey());
		textSecretKey.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				isValid();
			}
		});
		textSecretKey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblQrcodeUrl = new Label(grpGoogleAuth, SWT.NONE);
		lblQrcodeUrl.setText("<a href='https://code.google.com/p/google-authenticator/' target='_blank'>" + Messages.get().UserInfoPerference_lblQrcodeUrl_text + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$
		lblQrcodeUrl.setData(RWT.MARKUP_ENABLED, Boolean.TRUE);
		
		textQRCodeURL = new Text(grpGoogleAuth, SWT.BORDER | SWT.WRAP | SWT.MULTI);
		GridData gd_textQRCodeURL = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textQRCodeURL.heightHint = 50;
		textQRCodeURL.setLayoutData(gd_textQRCodeURL);
		
		if(btnGetOptCode.getSelection()) {
			String strEmail = textEmail.getText();
			String[] strUserDomain = StringUtils.split(strEmail, "@"); //$NON-NLS-1$
			String strURL = GetOTPCode.getURL(strUserDomain[0], strUserDomain[1], secretKey);
			
			textQRCodeURL.setText(strURL);
		}
		
		Label lblOptCode = new Label(grpGoogleAuth, SWT.NONE);
		lblOptCode.setText(Messages.get().OTP);
		
		textOTPCode = new Text(grpGoogleAuth, SWT.BORDER);
		textOTPCode.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				isValid();
			}
		});
		textOTPCode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		if(!SessionManager.isSystemAdmin()) {
			Button buttonWithdrawal = new Button(container, SWT.NONE);
			buttonWithdrawal.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if(MessageDialog.openConfirm(null, CommonMessages.get().Confirm, Messages.get().UserInfoPerference_9)) { //$NON-NLS-1$
						try {
							TadpoleSystem_UserRole.withdrawal(SessionManager.getUserSeq());
							
//							TadpoleSQLTransactionManager.executeRollback(SessionManager.getEMAIL());
							SessionManager.logout(SessionManager.getEMAIL());
						} catch (Exception e1) {
							logger.error("user withdrawal", e1); //$NON-NLS-1$
						}
					}
						
				}
			});
			buttonWithdrawal.setText(Messages.get().UserInfoPerference_button_text);
			new Label(container, SWT.NONE);
		}
		
		initUI();
	}
	
	/**
	 * initialize ui
	 */
	private void initUI() {
		String strLan = SessionManager.getLangeage();
		comboLanguage.setText(new Locale(strLan).getDisplayLanguage());
	}
	
	/**
	 * generate google otp 
	 */
	private void generateGoogleOTP() {
		if(!btnGetOptCode.getSelection()) {
			textSecretKey.setText(""); //$NON-NLS-1$
			textQRCodeURL.setText(""); //$NON-NLS-1$
			
			return;
		}
		secretKey = GetOTPCode.getSecretKey();
		
		String strEmail = textEmail.getText();
		String[] strUserDomain = StringUtils.split(strEmail, "@"); //$NON-NLS-1$
		String strID = strUserDomain[0];
		String strDomain = "";
		if(strUserDomain.length == 1) {
			strDomain = "gen.tadpolehub.com";
		} else {
			strDomain = strUserDomain[1];
		}
		String strURL = GetOTPCode.getURL(strID, strDomain, secretKey);
		if(logger.isDebugEnabled()) {
			logger.debug("user is " + strID + ", domain is " + strDomain + ", secretkey is " + secretKey); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			logger.debug("url is " + strURL); //$NON-NLS-1$
		}
		
		textSecretKey.setText(secretKey);
		textQRCodeURL.setText(strURL);
		
		// google analytic
		AnalyticCaller.track(this.getClass().getName());
	}
	
	@Override
	public boolean isValid() {
		if(StringUtils.equals(GetAdminPreference.getLoginMethod(), AdminPreferenceDefine.SYSTEM_LOGIN_METHOD_LDAP)) {
			if("".equals(textName.getText())) {
				textName.setFocus();
				
				setValid(false);
				setErrorMessage(Messages.get().PleaseInputName);
				return false;	
			} else if("".equals(textEmail.getText())) {
				textEmail.setFocus();
				
				setValid(false);
				setErrorMessage(Messages.get().PleaseInputEamil);
				return false;
			}
			
			setErrorMessage(null);
			setValid(true);
			
		} else {
			if(!TadpoleApplicationContextManager.isPersonOperationType()) {
				String otpSecretKey = StringUtils.trim(textSecretKey.getText());
				if(btnGetOptCode.getSelection()) {
					if("".equals(textOTPCode.getText())) { //$NON-NLS-1$
						textOTPCode.setFocus();
	
						setValid(false);
						setErrorMessage(Messages.get().UserInfoPerference_15);
						return false;
					}
					
					try {
						GetOTPCode.isValidate(SessionManager.getEMAIL(), otpSecretKey, textOTPCode.getText());
					} catch(Exception e) {
						textOTPCode.setFocus();
	
						setValid(false);
						setErrorMessage(Messages.get().UserInfoPerference_16);
						return false;
					}
				}
				
				setErrorMessage(null);
				setValid(true);
			}
		}
		
		return true;
	}
	
	@Override
	public boolean performOk() {
		
		if(StringUtils.equals(GetAdminPreference.getLoginMethod(), AdminPreferenceDefine.SYSTEM_LOGIN_METHOD_LDAP)) {
			
			
			UserDAO user = new UserDAO();
			user.setSeq(SessionManager.getUserSeq());
			user.setName(textName.getText());
			user.setEmail(textEmail.getText());
			
			try {
				TadpoleSystem_UserQuery.updateUserNameEmail(user);
				
				SessionManager.updateSessionAttribute(SessionManager.NAME.LOGIN_NAME.name(), textName.getText());			
				SessionManager.updateSessionAttribute(SessionManager.NAME.LOGIN_EMAIL.name(), textEmail.getText());
			} catch (Exception e) {
				logger.error("user info change", e); //$NON-NLS-1$
				MessageDialog.openError(getShell(),CommonMessages.get().Error, e.getMessage());			 //$NON-NLS-1$
				
				return false;
			}
			
		} else {
			if(!TadpoleApplicationContextManager.isPersonOperationType()) {
				String useOTP = btnGetOptCode.getSelection()?"YES":"NO"; //$NON-NLS-1$ //$NON-NLS-2$  
				String otpSecretKey = StringUtils.trim(textSecretKey.getText());
				Locale locale = Locale.ENGLISH;
				String timezone = StringUtils.trim(comboTimezone.getText());
				if(comboLanguage.getData(comboLanguage.getText()) != null) {
					locale = (Locale)comboLanguage.getData(comboLanguage.getText());	
				}
				
				UserDAO user = new UserDAO();
				user.setSeq(SessionManager.getUserSeq());
				
				user.setUse_otp(useOTP);
				user.setOtp_secret(otpSecretKey);
				
				user.setLanguage(locale.toLanguageTag());
				user.setTimezone(timezone);
				
				try {
					TadpoleSystem_UserQuery.updateUserBasic(user);
					
//					SessionManager.updateSessionAttribute(SessionManager.NAME.LOGIN_PASSWORD.name(), rePass);			
					TadpoleSystem_UserQuery.updateUserOTPCode(user);
					SessionManager.updateSessionAttribute(SessionManager.NAME.USE_OTP.name(), useOTP);			
					SessionManager.updateSessionAttribute(SessionManager.NAME.OTP_SECRET_KEY.name(), otpSecretKey);
					SessionManager.updateSessionAttribute(SessionManager.NAME.LANGUAGE.name(), locale.toLanguageTag());
					SessionManager.updateSessionAttribute(SessionManager.NAME.TIMEZONE.name(), timezone);
					
					//fix https://github.com/hangum/TadpoleForDBTools/issues/243
//					SessionManager.setPassword(rePass);
				} catch (Exception e) {
					logger.error("user info change", e); //$NON-NLS-1$
					MessageDialog.openError(getShell(),CommonMessages.get().Error, e.getMessage());			 //$NON-NLS-1$
					
					return false;
				}
			}
		}
		
		return super.performOk();
	}
	
	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
	}

}
