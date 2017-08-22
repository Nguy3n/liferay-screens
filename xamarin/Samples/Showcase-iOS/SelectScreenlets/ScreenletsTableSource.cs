﻿using System;
using Foundation;
using UIKit;
using ShowcaseiOS.ViewController;

namespace ShowcaseiOS.SelectScreenlets
{
    public class ScreenletsTableSource: UITableViewSource
    {
        protected string[] screenlets;
        protected string cellIdentifier = "TableCell";
        SelectScreenletViewController owner;

        public ScreenletsTableSource(string[] data, SelectScreenletViewController owner)
        {
            screenlets = data;
            this.owner = owner;
        }

        /* UITableViewSource */

        public override UITableViewCell GetCell(UITableView tableView, NSIndexPath indexPath)
        {
            UITableViewCell cell = tableView.DequeueReusableCell(cellIdentifier);

            if (cell == null)
            {
                cell = new UITableViewCell(UITableViewCellStyle.Default, cellIdentifier);
                cell.TextLabel.Text = screenlets[indexPath.Row];
                cell.Accessory = UITableViewCellAccessory.DisclosureIndicator;
            }

            return cell;
        }

        public override nint RowsInSection(UITableView tableview, nint section)
        {
            return screenlets.Length;
        }

        /* UITableView */

        public override void RowSelected(UITableView tableView, Foundation.NSIndexPath indexPath)
        {
            UIAlertController okAlertController = UIAlertController.Create("Row Selected", screenlets[indexPath.Row], UIAlertControllerStyle.Alert);
            okAlertController.AddAction(UIAlertAction.Create("OK", UIAlertActionStyle.Default, null));
            owner.PresentViewController(okAlertController, true, null);
            tableView.DeselectRow(indexPath, true);
        }
    }
}
