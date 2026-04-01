package br.com.ekklesia.ekklesia_admin_api.tenant;

public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_CHURCH = new ThreadLocal<>();

    public static void setChurchId(Long churchId) {
        CURRENT_CHURCH.set(churchId);
    }

    public static Long getChurchId() {
        return CURRENT_CHURCH.get();
    }

    public static void clear() {
        CURRENT_CHURCH.remove();
    }
}
