package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.server.child;

import java.util.Calendar;

import org.greatfree.cluster.ChildTask;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.CommerceApplicationID;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.CustomerRegistryRequest;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.CustomerRegistryResponse;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.CustomerSignInRequest;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.CustomerSignInResponse;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.CustomerSignOutRequest;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.CustomerSignOutResponse;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.FollowMerchandiseNotification;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.FollowVendorNotification;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.InterFollowMerchandiseNotification;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.InterFollowVendorNotification;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.OpenShopRequest;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.PollMerchandiseRequest;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.PollMerchandiseResponse;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.PollVendorRequest;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.PollVendorResponse;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.VendorRegistryRequest;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.VendorRegistryResponse;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.VendorSignInRequest;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.VendorSignInResponse;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.VendorSignOutRequest;
import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.VendorSignOutResponse;
import org.greatfree.dip.cs.multinode.server.CSAccount;
import org.greatfree.dip.cs.twonode.server.AccountRegistry;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

// Created: 07/18/2019, Bing Li
class CommerceTask implements ChildTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CommerceApplicationID.FOLLOW_VENDOR_NOTIFICATION:
				// The vendor receives the intercast following notification from the customer child. The account information of the customer is enclosed in the notification. 07/21/2019, Bing Li
				InterFollowVendorNotification ifvn = (InterFollowVendorNotification)notification;
				AccountRegistry.CS().add(ifvn.getAccount());
				FollowVendorNotification fvn = (FollowVendorNotification)ifvn.getIntercastNotification();
				System.out.println("processNotification(): FOLLOW_VENDOR_NOTIFICATION received @" + Calendar.getInstance().getTime());
				VendorRegistry.CS().followVendor(fvn.getVendorKey(), fvn.getCustomerKey());
				break;
				
			case CommerceApplicationID.FOLLOW_MERCHANDISE_NOTIFICATION:
				InterFollowMerchandiseNotification ifmn = (InterFollowMerchandiseNotification)notification;
				AccountRegistry.CS().add(ifmn.getAccount());
				FollowMerchandiseNotification fmn = (FollowMerchandiseNotification)ifmn.getIntercastNotification();
				System.out.println("processNotification(): FOLLOW_MERCHANDISE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				MerchandiseRegistry.CS().followMerchandise(fmn.getMerchandiseKey(), fmn.getCustomerKey());
				break;
				
				
		}
		
	}

	@Override
	public MulticastResponse processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case CommerceApplicationID.VENDOR_REGISTRY_REQUEST:
				VendorRegistryRequest vrr = (VendorRegistryRequest)request;
				System.out.println("processRequest(): VENDOR_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				AccountRegistry.CS().add(new CSAccount(vrr.getVendorKey(), vrr.getVendorName(), vrr.getDescription()));
				return new VendorRegistryResponse(vrr.getCollaboratorKey(), true);
				
			case CommerceApplicationID.CUSTOMER_REGISTRY_REQUEST:
				CustomerRegistryRequest crr = (CustomerRegistryRequest)request;
				System.out.println("processRequest(): CUSTOMER_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				AccountRegistry.CS().add(new CSAccount(crr.getCustomerKey(), crr.getCustomerName(), crr.getDescription()));
				return new CustomerRegistryResponse(crr.getCollaboratorKey(), true);
				
			case CommerceApplicationID.VENDOR_SIGN_IN_REQUEST:
				VendorSignInRequest vsir = (VendorSignInRequest)request;
				if (AccountRegistry.CS().isAccountExisted(vsir.getVendorKey()))
				{
					return new VendorSignInResponse(vsir.getCollaboratorKey(), true);
				}
				else
				{
					return new VendorSignInResponse(vsir.getCollaboratorKey(), false);
				}

			case CommerceApplicationID.CUSTOMER_SIGN_IN_REQUEST:
				CustomerSignInRequest csir = (CustomerSignInRequest)request;
				System.out.println("processRequest(): CUSTOMER_SIGN_IN_REQUEST received @" + Calendar.getInstance().getTime());
				if (AccountRegistry.CS().isAccountExisted(csir.getCustomerKey()))
				{
					return new CustomerSignInResponse(csir.getCollaboratorKey(), true);
				}
				else
				{
					return new CustomerSignInResponse(csir.getCollaboratorKey(), false);
				}
				
			case CommerceApplicationID.VENDOR_SIGN_OUT_REQUEST:
				VendorSignOutRequest vsor = (VendorSignOutRequest)request;
				System.out.println("processRequest(): VENDOR_SIGN_OUT_REQUEST received @" + Calendar.getInstance().getTime());
				return new VendorSignOutResponse(vsor.getCollaboratorKey(), true);
				
			case CommerceApplicationID.CUSTOMER_SIGN_OUT_REQUEST:
				CustomerSignOutRequest csor = (CustomerSignOutRequest)request;
				System.out.println("processRequest(): CUSTOMER_SIGN_OUT_REQUEST received @" + Calendar.getInstance().getTime());
				return new CustomerSignOutResponse(csor.getCollaboratorKey(), true);
				
			case CommerceApplicationID.POLL_MERCHANDISE_REQUEST:
				PollMerchandiseRequest pmr = (PollMerchandiseRequest)request;
				System.out.println("processRequest(): POLL_MERCHANDISE_REQUEST received @" + Calendar.getInstance().getTime());
				if (AccountRegistry.CS().isAccountExisted(pmr.getCustomerKey()))
				{
					if (MerchandiseRegistry.CS().isFollowerExisted(pmr.getMerchandiseKey(), pmr.getCustomerKey()))
					{
						System.out.println("CommerceTask-processRequest(): customer name = " + AccountRegistry.CS().getUserName(pmr.getCustomerKey()) + " is polling the merchandise, " + MerchandiseRegistry.CS().getMerchandise(pmr.getMerchandiseKey()).getMerchandiseName());
						return new PollMerchandiseResponse(MerchandiseStore.FILLED().getPosts(pmr.getMerchandiseKey(), pmr.getMerchandisePostCount(), pmr.getTimespan()), pmr.getCollaboratorKey());
					}
					else
					{
						System.out.println("CommerceTask-processRequest(): polling user = " + AccountRegistry.CS().getUserName(pmr.getCustomerKey()) + ", does NOT follow the merchandise ...");
					}
				}
				else
				{
					System.out.println("CommerceTask-processRequest(): polling user = " + AccountRegistry.CS().getUserName(pmr.getCustomerKey()) + ", does NOT register the system ...");
				}
				return new PollMerchandiseResponse(null, pmr.getCollaboratorKey());
				
			case CommerceApplicationID.POLL_VENDOR_REQUEST:
				PollVendorRequest pvr = (PollVendorRequest)request;
				System.out.println("processRequest(): POLL_VENDOR_REQUEST received @" + Calendar.getInstance().getTime());
				if (AccountRegistry.CS().isAccountExisted(pvr.getCustomerKey()))
				{
					if (VendorRegistry.CS().isFollowerExisted(pvr.getVendorKey(), pvr.getCustomerKey()))
					{
						System.out.println("CommerceTask-processRequest(): customer name = " + AccountRegistry.CS().getUserName(pvr.getCustomerKey()) + " is polling the vendor, " + AccountRegistry.CS().getUserName(pvr.getVendorKey()));
						return new PollVendorResponse(Market.FILLED().getPosts(pvr.getVendorKey(), pvr.getVendorPostCount(), pvr.getTimespan()), pvr.getCollaboratorKey());
					}
					else
					{
						System.out.println("CommerceTask-processRequest(): polling user = " + AccountRegistry.CS().getUserName(pvr.getCustomerKey()) + ", does NOT follow the vendor ...");
					}
				}
				else
				{
					System.out.println("CommerceTask-processRequest(): polling user = " + AccountRegistry.CS().getUserName(pvr.getCustomerKey()) + ", does NOT register the system ...");
				}
				return new PollVendorResponse(null, pvr.getCollaboratorKey());
		}
		
		return null;
	}

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case CommerceApplicationID.FOLLOW_VENDOR_NOTIFICATION:
				FollowVendorNotification fvn = (FollowVendorNotification)notification;
				System.out.println("prepareNotification(): FOLLOW_VENDOR_NOTIFICATION received @" + Calendar.getInstance().getTime());
				System.out.println("prepareNotification(): user name = " + AccountRegistry.CS().getUserName(fvn.getCustomerKey()) + " is following the vendor, " + AccountRegistry.CS().getUserName(fvn.getVendorKey()));
				VendorRegistry.CS().followVendor(fvn.getVendorKey(), fvn.getCustomerKey());
				return new InterFollowVendorNotification(fvn, AccountRegistry.CS().getAccount(fvn.getCustomerKey()));
				
			case CommerceApplicationID.FOLLOW_MERCHANDISE_NOTIFICATION:
				FollowMerchandiseNotification fmn = (FollowMerchandiseNotification)notification;
				System.out.println("prepareNotification(): FOLLOW_MERCHANDISE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				System.out.println("prepareNotification(): user name = " + AccountRegistry.CS().getUserName(fmn.getCustomerKey()) + " is following the merchandise, " + MerchandiseRegistry.CS().getMerchandise(fmn.getMerchandiseKey()));
				MerchandiseRegistry.CS().followMerchandise(fmn.getMerchandiseKey(), fmn.getCustomerKey());
				return new InterFollowMerchandiseNotification(fmn, AccountRegistry.CS().getAccount(fmn.getCustomerKey()));
		}
		
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		switch (request.getApplicationID())
		{
			case CommerceApplicationID.OPEN_SHOP_REQUEST:
				OpenShopRequest osr = (OpenShopRequest)request;
		
				System.out.println("prepareRequest(): OPEN_SHOP_REQUEST received @" + Calendar.getInstance().getTime());
				System.out.println("prepareRequest(): osr = " + osr.getApplicationID());
				
				break;
		}
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processResponse(Response response)
	{
		// TODO Auto-generated method stub
		
	}

}
