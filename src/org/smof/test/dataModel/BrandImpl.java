package org.smof.test.dataModel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.smof.annnotations.SmofArray;
import org.smof.annnotations.SmofDate;
import org.smof.annnotations.SmofNumber;
import org.smof.element.AbstractElement;
import org.smof.parsers.SmofType;

class BrandImpl extends AbstractElement implements Brand {
	
	@SmofArray(name = OWNERS, type = SmofType.STRING)
	private final List<String> owners;
	
	@SmofDate(name = FOUNDING)
	private final LocalDate founding;
	
	@SmofNumber(name = CAPITAL)
	private long capital;

	BrandImpl(String... owners) {
		super();
		this.owners = Arrays.asList(owners);
		this.founding = LocalDate.now();
		this.capital = 0;
	}

	@Override
	public List<String> getOwners() {
		return owners;
	}

	@Override
	public LocalDate getFoundingDate() {
		return founding;
	}

	@Override
	public long getCapital() {
		return capital;
	}

	@Override
	public void setCapital(long value) {
		this.capital = value;
	}

	@Override
	public void increaseCapital(long value) {
		this.capital += value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (capital ^ (capital >>> 32));
		result = prime * result + ((founding == null) ? 0 : founding.hashCode());
		result = prime * result + ((owners == null) ? 0 : owners.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BrandImpl other = (BrandImpl) obj;
		if (capital != other.capital) {
			return false;
		}
		if (founding == null) {
			if (other.founding != null) {
				return false;
			}
		} else if (!founding.equals(other.founding)) {
			return false;
		}
		if (owners == null) {
			if (other.owners != null) {
				return false;
			}
		} else if (!owners.equals(other.owners)) {
			return false;
		}
		return true;
	}

}