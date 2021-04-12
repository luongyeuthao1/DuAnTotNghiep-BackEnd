package vn.fpt.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import vn.fpt.web.rest.TestUtil;

public class ApplyTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Apply.class);
        Apply apply1 = new Apply();
        apply1.setId(1L);
        Apply apply2 = new Apply();
        apply2.setId(apply1.getId());
        assertThat(apply1).isEqualTo(apply2);
        apply2.setId(2L);
        assertThat(apply1).isNotEqualTo(apply2);
        apply1.setId(null);
        assertThat(apply1).isNotEqualTo(apply2);
    }
}
