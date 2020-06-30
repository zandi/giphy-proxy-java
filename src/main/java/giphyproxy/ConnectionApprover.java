package giphyproxy;

import java.util.ArrayList;

import java.lang.UnsupportedOperationException;

public class ConnectionApprover {
	private ArrayList<ProxyTarget> approvedTargets;

	ConnectionApprover() {
		this.approvedTargets = new ArrayList<ProxyTarget>();
	}

	public void addApprovedTarget(ProxyTarget target) {
		this.approvedTargets.add(target);
	}

	public void removeTarget(ProxyTarget target) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Removing approved targets unimplemented. Destroy and recreate the object for now");
	}

	public boolean isApproved(ProxyTarget target) {
		return this.approvedTargets.contains(target);
	}
}
