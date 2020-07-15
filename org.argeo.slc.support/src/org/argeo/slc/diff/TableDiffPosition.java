package org.argeo.slc.diff;

import org.argeo.slc.UnsupportedException;

/**
 * A diff position within a table structure such a CSV file or an SQL result
 * set.
 */
public class TableDiffPosition extends DiffPosition {
	private Integer line;
	/** Can be null */
	private Integer column;
	/** Can be null */
	private String columnName;

	public TableDiffPosition(RelatedFile relatedFile, Integer line,
			Integer column, String columnName) {
		super(relatedFile);
		this.line = line;
		this.column = column;
		this.columnName = columnName;
	}

	@SuppressWarnings("unused")
	private TableDiffPosition() {
	}

	public Integer getLine() {
		return line;
	}

	public Integer getColumn() {
		return column;
	}

	public String getColumnName() {
		return columnName;
	}

	public int compareTo(DiffPosition dp) {
		if (!(dp instanceof TableDiffPosition))
			throw new UnsupportedException("position", dp);

		TableDiffPosition o = (TableDiffPosition) dp;
		if (relatedFile.equals(o.relatedFile)) {
			if (line == o.line) {
				return column.compareTo(o.column);
			} else {
				return line.compareTo(o.line);
			}
		} else {
			return relatedFile.compareTo(o.relatedFile);
		}
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("");
		buf.append(relatedFile).append('[').append(line);
		if (column != null) {
			buf.append(',').append(column);
			if (columnName != null) {
				buf.append('-').append(columnName);
			}
		}
		buf.append(']');
		return buf.toString();
	}

	// Hibernate
	@SuppressWarnings("unused")
	private void setLine(Integer line) {
		this.line = line;
	}

	@SuppressWarnings("unused")
	private void setColumn(Integer column) {
		this.column = column;
	}

	@SuppressWarnings("unused")
	private void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}
