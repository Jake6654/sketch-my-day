"use client";

type BirthdaySurpriseProps = {
  open: boolean;
};

const PARTY_ITEMS = [
  { icon: "🎉", left: "8%", top: "20%", delay: "0ms" },
  { icon: "🎂", left: "20%", top: "12%", delay: "120ms" },
  { icon: "🎈", left: "32%", top: "24%", delay: "240ms" },
  { icon: "✨", left: "44%", top: "10%", delay: "360ms" },
  { icon: "🎊", left: "56%", top: "22%", delay: "480ms" },
  { icon: "🥳", left: "68%", top: "14%", delay: "600ms" },
  { icon: "🎁", left: "80%", top: "24%", delay: "720ms" },
  { icon: "✨", left: "90%", top: "16%", delay: "840ms" },
];

export function isFriendBirthday(dateString: string): boolean {
  return dateString.slice(5) === "01-10";
}

export default function BirthdaySurprise({ open }: BirthdaySurpriseProps) {
  if (!open) return null;

  return (
    <div className="fixed inset-0 z-[120] pointer-events-none">
      <div className="absolute inset-0 bg-black/25 animate-pulse" />

      {PARTY_ITEMS.map((item, index) => (
        <span
          key={`${item.icon}-${index}`}
          className="absolute text-3xl md:text-5xl animate-bounce"
          style={{
            left: item.left,
            top: item.top,
            animationDelay: item.delay,
          }}
        >
          {item.icon}
        </span>
      ))}

      <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 px-6 py-4 bg-[#FFD23F] border-4 border-black rounded-2xl shadow-[8px_8px_0px_rgba(0,0,0,1)] text-center animate-bounce">
        <p className="text-xl md:text-2xl font-black">Happy Birthday! 🎂</p>
        <p className="text-sm md:text-base font-bold mt-1">
          문예은 생일 축하한다구👊🏻
        </p>
      </div>
    </div>
  );
}
