package com.Bhawesh.expense_tracker.enums;

public enum StatementStatus {
    PENDING, PARSED, IMPORTED, FAILED,
    /** Kept only so existing rows created by the previous immediate-import flow remain readable. */
    SUCCESS
}
