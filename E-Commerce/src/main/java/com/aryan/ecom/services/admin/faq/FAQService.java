package com.aryan.ecom.services.admin.faq;

import com.aryan.ecom.dto.FAQDto;

public interface FAQService {
	FAQDto postFAQ(Long productId, FAQDto faqDto);
}
