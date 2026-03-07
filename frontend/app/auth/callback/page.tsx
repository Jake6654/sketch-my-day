"use client";

import { useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { supabase } from "@/lib/supabaseClient";

export default function AuthCallback() {
  const router = useRouter();
  const searchParams = useSearchParams();

  // OAuth callback: authorization code를 세션으로 교환한 뒤 홈으로 이동
  useEffect(() => {
    const handleCallback = async () => {
      const code = searchParams.get("code");
      const errorDescription = searchParams.get("error_description");

      if (errorDescription) {
        console.error("OAuth callback error:", errorDescription);
        alert(`로그인에 실패했습니다: ${errorDescription}`);
        router.replace("/");
        return;
      }

      if (code) {
        const { error } = await supabase.auth.exchangeCodeForSession(code);
        if (error) {
          console.error("Failed to exchange auth code:", error.message);
          alert("로그인 세션 생성에 실패했습니다.");
        }
      }

      router.replace("/");
    };

    handleCallback();
  }, [router, searchParams]);

  return (
    <div className="flex justify-center items-center h-screen">
      Processing login...
    </div>
  );
}
