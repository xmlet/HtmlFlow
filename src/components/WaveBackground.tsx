import { useColorMode } from '@docusaurus/theme-common';

export function WaveBackground() {
  const { colorMode } = useColorMode();

  return (
    <div className="absolute inset-0 overflow-hidden pointer-events-none">
      <svg
        className="absolute w-full h-full"
        style={{ transform: 'translateY(250px)' }}
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 1440 500"
        preserveAspectRatio="none"
      >
        <defs>
          <linearGradient id="waveGradient" x1="0%" y1="0%" x2="0%" y2="100%">
            {colorMode === 'dark' ? (
              <>
                <stop offset="0%" stopColor="#00BCFF" />
                <stop offset="90%" stopColor="#1b3f88" />
              </>
            ) : (
              <>
                <stop offset="0%" stopColor="#99CCFF" />
                <stop offset="90%" stopColor="#93afe7" />
              </>
            )}
          </linearGradient>
        </defs>
        <path
          fill="url(#waveGradient)"
          fill-opacity="1"
          d="M0,64L48,101.3C96,139,192,213,288,240C384,267,480,245,576,213.3C672,181,768,139,864,138.7C960,139,1056,181,1152,165.3C1248,149,1344,75,1392,37.3L1440,0L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"
        ></path>
      </svg>
    </div>
  );
}
