import {useEffect, useState} from "react";
import {getCurrentUser} from "../../api/userApi.ts";
import type {UserPublic} from "../../types/UserPublic.ts";

const UserProfile = () => {
    const [user, setUser] = useState<UserPublic | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        getCurrentUser()
            .then(setUser)
            .catch((err) => {
                console.error(err);
                setError("could not load profile");
            });
    }, []);

    if(error) return <p style={{color: "red"}}>{error}</p>;
    if(!user) return <p>Loading...</p>;


    return(
        <div>
            <img
                src={user.profilePhotoUrl}
                alt="Profile"
                style={{width: 150, borderRadius: "50%"}}
            />
            <h2>{user.firstName} {user.lastName}</h2>
            <p><strong>Bio:</strong> {user.bio || "No bio provided"}</p>
        </div>
    );
};

export default UserProfile;