package com.joutio.identity.application.service;

import com.joutio.identity.domain.model.LoginCode;
import com.joutio.identity.domain.port.in.FindUserUseCase;
import com.joutio.identity.domain.port.in.RecordUserLoginUseCase;
import com.joutio.identity.domain.port.in.RegisterUserUseCase;
import com.joutio.identity.domain.port.out.EmailSenderPort;
import com.joutio.identity.domain.port.out.LoginCodeRepositoryPort;
import com.joutio.identity.infrastructure.properties.AppProperties;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Code fixe des joueurs synthétiques (tests de charge) : réservé au domaine configuré, sans email.
 */
class PasswordlessAuthServiceTest {

    private static final String DOMAIN = "loadtest.joutio.com";
    private static final String CODE = "424242";

    /** Encodeur réversible pour lire le code stocké dans les assertions. */
    private static final PasswordEncoder PLAIN = new PasswordEncoder() {
        @Override
        public String encode(CharSequence raw) {
            return "plain:" + raw;
        }

        @Override
        public boolean matches(CharSequence raw, String encoded) {
            return encode(raw).equals(encoded);
        }
    };

    private final LoginCodeRepositoryPort loginCodes = mock(LoginCodeRepositoryPort.class);
    private final EmailSenderPort emailSender = mock(EmailSenderPort.class);

    @Test
    void reservedDomain_storesFixedCodeWithoutEmail() {
        PasswordlessAuthService service = service(DOMAIN, CODE);

        service.requestCode("LT-0001@LoadTest.Joutio.com");

        assertThat(savedCode().codeHash()).isEqualTo("plain:" + CODE);
        assertThat(savedCode().email()).isEqualTo("lt-0001@loadtest.joutio.com");
        verify(emailSender, never()).sendLoginCode(anyString(), anyString(), any());
    }

    @Test
    void otherDomain_keepsRandomCodeAndEmail() {
        PasswordlessAuthService service = service(DOMAIN, CODE);

        service.requestCode("player@gmail.com");

        verify(emailSender).sendLoginCode(eq("player@gmail.com"), anyString(), any());
    }

    @Test
    void lookalikeDomain_isNotReserved() {
        PasswordlessAuthService service = service(DOMAIN, CODE);

        service.requestCode("player@evil-loadtest.joutio.com");

        verify(emailSender).sendLoginCode(eq("player@evil-loadtest.joutio.com"), anyString(), any());
    }

    @Test
    void disabled_reservedDomainIsAnOrdinaryEmail() {
        PasswordlessAuthService service = service(DOMAIN, "");

        service.requestCode("lt-0001@loadtest.joutio.com");

        verify(emailSender).sendLoginCode(eq("lt-0001@loadtest.joutio.com"), anyString(), any());
    }

    private LoginCode savedCode() {
        ArgumentCaptor<LoginCode> captor = ArgumentCaptor.forClass(LoginCode.class);
        verify(loginCodes).save(captor.capture());
        return captor.getValue();
    }

    private PasswordlessAuthService service(String domain, String code) {
        when(loginCodes.findByEmail(anyString())).thenReturn(Optional.empty());
        AppProperties properties = new AppProperties(
                new AppProperties.AuthorizationServer("", ""),
                new AppProperties.Security("login",
                        new AppProperties.Security.OAuth2("ok", "ko"),
                        new AppProperties.Security.Cors(List.of()),
                        new AppProperties.Security.Session("AUTH_TX", "Lax", false, "/")),
                new AppProperties.Mail("", "from", "base-url"),
                new AppProperties.Jwk("", false),
                new AppProperties.Auth("", new AppProperties.Auth.Mobile("mobile", "", ""),
                        new AppProperties.Auth.LoadTest(domain, code)),
                new AppProperties.SeedData(false));
        return new PasswordlessAuthService(loginCodes, emailSender,
                mock(FindUserUseCase.class), mock(RegisterUserUseCase.class),
                mock(RecordUserLoginUseCase.class), PLAIN, properties);
    }
}
