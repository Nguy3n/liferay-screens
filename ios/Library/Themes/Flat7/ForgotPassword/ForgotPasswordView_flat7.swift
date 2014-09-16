/**
* Copyright (c) 2000-present Liferay, Inc. All rights reserved.
*
* This library is free software; you can redistribute it and/or modify it under
* the terms of the GNU Lesser General Public License as published by the Free
* Software Foundation; either version 2.1 of the License, or (at your option)
* any later version.
*
* This library is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
* details.
*/
import UIKit


public class ForgotPasswordView_flat7: ForgotPasswordView_default {

	@IBOutlet var titleLabel: UILabel?
	@IBOutlet var subtitleLabel: UILabel?
	@IBOutlet var userNamePlaceholder: UILabel?

	override internal func onSetTranslations() {
		let bundle = NSBundle(forClass: self.dynamicType)

		titleLabel!.text = NSLocalizedString("theme-flat7-forgotpassword-title", tableName: "flat7", bundle: bundle, value: "", comment: "")
		subtitleLabel!.text = NSLocalizedString("theme-flat7-forgotpassword-subtitle", tableName: "flat7", bundle: bundle, value: "", comment: "")
		userNamePlaceholder!.text = NSLocalizedString("theme-flat7-forgotpassword-email", tableName: "flat7", bundle: bundle, value: "", comment: "")

		let str = requestPasswordButton!.attributedTitleForState(UIControlState.Normal)
		let translated = NSLocalizedString("theme-flat7-forgotpassword-request", tableName: "flat7", bundle: bundle, value: "", comment: "")
		let newStr = NSMutableAttributedString(attributedString: str!)
		newStr.replaceCharactersInRange(NSMakeRange(0, str!.length), withString:translated)
		requestPasswordButton!.setAttributedTitle(newStr, forState: UIControlState.Normal)

		userNameField!.placeholder = "";
	}

	override public func setUserName(userName: String) {
		super.setUserName(userName)
		showPlaceholder(userNamePlaceholder!, show:userName == "")
	}

	internal func textField(textField: UITextField!, shouldChangeCharactersInRange range: NSRange, replacementString string: String!) -> Bool {

		let newText = (textField.text as NSString).stringByReplacingCharactersInRange(range, withString:string)

		showPlaceholder(userNamePlaceholder!, show:newText == "")

		return true
	}

	private func showPlaceholder(placeholder:UILabel, show:Bool) {
		UIView.animateWithDuration(0.4, animations: {
			placeholder.alpha = show ? 1.0 : 0.0
		})
	}

}
