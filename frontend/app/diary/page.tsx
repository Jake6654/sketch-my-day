// app/diary/page.tsx
"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function DiaryPage() {
  const router = useRouter();

  useEffect(() => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    const day = String(now.getDate()).padStart(2, "0");
    router.replace(`/diary/${year}-${month}-${day}`);
  }, [router]);

  return <div className="p-8 font-bold">Redirecting to today&apos;s diary...</div>;
}
