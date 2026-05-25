package com.codethatmakessense.heavyaggregate.publish;

import com.codethatmakessense.heavyaggregate.Decision;

interface PublishGuard {
    Decision check(PublishCheck context);
}
