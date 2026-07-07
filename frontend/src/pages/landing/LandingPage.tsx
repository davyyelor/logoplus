import "./landing.css";
import { LandingHero } from "./LandingHero";
import { LandingBenefits } from "./LandingBenefits";
import { LandingFeatures } from "./LandingFeatures";
import { LandingAudiences } from "./LandingAudiences";
import { LandingPricing } from "./LandingPricing";
import { LandingFaq } from "./LandingFaq";
import { LandingCta } from "./LandingCta";
import { LandingFooter } from "./LandingFooter";

/**
 * Public marketing landing page served at `/`. Requires no authentication and
 * links to the existing login for both clinic and family access.
 */
export function LandingPage() {
  return (
    <div className="landing">
      <LandingHero />
      <main>
        <LandingBenefits />
        <LandingFeatures />
        <LandingAudiences />
        <LandingPricing />
        <LandingFaq />
        <LandingCta />
      </main>
      <LandingFooter />
    </div>
  );
}
