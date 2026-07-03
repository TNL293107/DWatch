package com.dwatch.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CsrfUtilTest {

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletRequest request;

    @Test
    void getToken_noExistingToken_generatesAndStoresOne() {
        when(session.getAttribute("csrfToken")).thenReturn(null);

        String token = CsrfUtil.getToken(session);

        assertThat(token).isNotBlank();
        verify(session).setAttribute(eq("csrfToken"), eq(token));
    }

    @Test
    void getToken_existingToken_reusesIt() {
        when(session.getAttribute("csrfToken")).thenReturn("existing-token");

        String token = CsrfUtil.getToken(session);

        assertThat(token).isEqualTo("existing-token");
        verify(session, never()).setAttribute(anyString(), any());
    }

    @Test
    void isValid_noSession_returnsFalse() {
        when(request.getSession(false)).thenReturn(null);

        assertThat(CsrfUtil.isValid(request)).isFalse();
    }

    @Test
    void isValid_matchingParamToken_returnsTrue() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn("abc123");
        when(request.getParameter("csrfToken")).thenReturn("abc123");

        assertThat(CsrfUtil.isValid(request)).isTrue();
    }

    @Test
    void isValid_mismatchedParamToken_returnsFalse() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn("abc123");
        when(request.getParameter("csrfToken")).thenReturn("wrong-token");

        assertThat(CsrfUtil.isValid(request)).isFalse();
    }

    @Test
    void isValid_fallsBackToHeaderWhenParamMissing() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn("abc123");
        when(request.getParameter("csrfToken")).thenReturn(null);
        when(request.getHeader("X-CSRF-Token")).thenReturn("abc123");

        assertThat(CsrfUtil.isValid(request)).isTrue();
    }

    @Test
    void isValid_missingSessionToken_returnsFalse() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("csrfToken")).thenReturn(null);
        when(request.getParameter("csrfToken")).thenReturn("abc123");

        assertThat(CsrfUtil.isValid(request)).isFalse();
    }
}
