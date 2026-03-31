import HeroSection from '../components/landing/HeroSection';
import FeaturesSection from '../components/landing/FeaturesSection';
import PricingSection from '../components/landing/PricingSection';
import TestimonialsSection from '../components/landing/TestimonialsSection';
import CTASection from '../components/landing/CTASection';
import Footer from '../components/landing/Footer';
import {features, pricingPlans, testimonials} from "../assets/data.js";
import {useClerk, useUser} from '@clerk/clerk-react';
import {useEffect} from "react";
import {useNavigate} from "react-router-dom";
import { Fence } from 'lucide-react';


const Landing = () => {
    const {openSignIn, openSignUp} = useClerk();
    const {isSignedIn} = useUser();
    const navigate = useNavigate();

    useEffect(() => {
        if (isSignedIn) {
            navigate("/dashboard");
        }
    }, [isSignedIn, navigate]);
    return (
        <div className="landing-page bg-gradient-to-b from-gray-50 to-gray-100">
            {/* Hero Section*/}
            <HeroSection openSignIn={openSignIn} openSignUp={openSignUp}/>
            {/* Features section*/}
            <FeaturesSection features={features}/>

            {/* Pricing section*/}
            <PricingSection pricingPlans={pricingPlans} openSignUp={openSignUp}/>

            {/* Testimonials section*/}
            <TestimonialsSection testimonials={testimonials}/>

            {/* CTA section*/}
            <CTASection openSignUp={openSignUp}/>

            {/* Footer section*/}
            <Footer />
        </div>
    )
}

export default Landing;