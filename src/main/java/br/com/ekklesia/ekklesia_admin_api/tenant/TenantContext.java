package br.com.ekklesia.ekklesia_admin_api.tenant;

public final class TenantContext {

    private TenantContext() {}
    private static final ThreadLocal<Long> CHURCH_ID = new ThreadLocal<>();

    public static void setChurchId(Long churchId) {
        CHURCH_ID.set(churchId);
    }

    public static Long getChurchId() {
        return CHURCH_ID.get();
    }

    public static void clear() {
        CHURCH_ID.remove();
    }
}
